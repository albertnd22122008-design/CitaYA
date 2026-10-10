const { onRequest } = require("firebase-functions/v2/https");
const { logger } = require("firebase-functions");
const admin = require("firebase-admin");
const Stripe = require("stripe");

if (!admin.apps.length) {
  admin.initializeApp();
}

// Precios en pesos dominicanos (DOP). El monto va en la unidad menor (centavos).
const PLANES = {
  PRO: { amount: 100000, currency: "dop" }, // RD$ 1,000.00 / mes
  VIP: { amount: 250000, currency: "dop" }, // RD$ 2,500.00 / mes
};

function getStripe() {
  const secret = process.env.STRIPE_SECRET_KEY;
  if (!secret) {
    throw new Error("STRIPE_SECRET_KEY no configurada en el backend");
  }
  return new Stripe(secret);
}

/**
 * Crea un PaymentIntent para el plan elegido (PRO o VIP).
 * La clave secreta se lee del entorno (Secret Manager); NUNCA del código ni del cliente.
 */
exports.crearSuscripcionVip = onRequest(
  { cors: true, secrets: ["STRIPE_SECRET_KEY"] },
  async (req, res) => {
    if (req.method !== "POST") {
      res.status(405).json({ error: "Método no permitido" });
      return;
    }
    try {
      const stripe = getStripe();
      const body = req.body || {};
      const negocioId = String(body.negocioId || "").trim();
      const plan = String(body.plan || "").trim().toUpperCase();

      if (!negocioId) {
        res.status(400).json({ error: "negocioId requerido" });
        return;
      }
      const cfg = PLANES[plan];
      if (!cfg) {
        res.status(400).json({ error: "Plan no válido: " + plan });
        return;
      }

      const intent = await stripe.paymentIntents.create({
        amount: cfg.amount,
        currency: cfg.currency,
        description: `CitaYa - Plan ${plan} (${negocioId})`,
        metadata: { negocioId, plan, dias: "30" },
        automatic_payment_methods: { enabled: true },
      });

      res.json({
        clientSecret: intent.client_secret,
        plan,
        amount: cfg.amount,
        currency: cfg.currency,
      });
    } catch (err) {
      logger.error("Error creando PaymentIntent", err);
      res.status(500).json({ error: err.message });
    }
  }
);

/**
 * Webhook de Stripe: activa el plan de forma fiable cuando el pago se confirma.
 * Configura el secreto con:  firebase functions:secrets:set STRIPE_WEBHOOK_SECRET
 */
exports.stripeWebhook = onRequest(
  { secrets: ["STRIPE_SECRET_KEY", "STRIPE_WEBHOOK_SECRET"] },
  async (req, res) => {
    let event;
    try {
      const stripe = getStripe();
      const signature = req.headers["stripe-signature"];
      event = stripe.webhooks.constructEvent(
        req.rawBody,
        signature,
        process.env.STRIPE_WEBHOOK_SECRET
      );
    } catch (err) {
      logger.error("Firma de webhook inválida", err);
      res.status(400).send(`Webhook Error: ${err.message}`);
      return;
    }

    try {
      if (event.type === "payment_intent.succeeded") {
        const intent = event.data.object;
        const negocioId = intent.metadata && intent.metadata.negocioId;
        const plan = intent.metadata && intent.metadata.plan;
        if (negocioId && plan) {
          const ahora = Date.now();
          const treintaDias = 30 * 24 * 60 * 60 * 1000;
          await admin.firestore().collection("negocios").doc(negocioId).set(
            {
              plan: plan,
              activo: true,
              fechaVencimientoPlan: new Date(ahora + treintaDias),
              fechaUltimoPago: new Date(ahora),
              ultimoPagoMonto: intent.amount,
              ultimoPagoMoneda: intent.currency,
            },
            { merge: true }
          );
          logger.info(`Plan ${plan} activado para ${negocioId}`);
        }
      }
      res.json({ received: true });
    } catch (err) {
      logger.error("Error procesando webhook", err);
      res.status(500).json({ error: err.message });
    }
  }
);
