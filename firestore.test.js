const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or write negocios", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("negocios").doc("biz_1").get());
  await assertFails(unauthDb.collection("negocios").doc("biz_1").set({
    nombre: "Hacker Shop",
    userId: "unauth",
    plan: "VIP_TRIAL"
  }));
});

test("Authenticated user: Alice can create and read her own negocio", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("negocios").doc("biz_alice").set({
    nombre: "Alice Barbershop",
    userId: ALICE_UID,
    plan: "VIP_TRIAL"
  }));

  const doc = await aliceDb.collection("negocios").doc("biz_alice").get();
  assertSucceeds(Promise.resolve(doc));
});

test("Cross-user isolation: Bob cannot update Alice's negocio", async () => {
  // Pre-seed Alice's negocio with admin context
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("negocios").doc("biz_alice").set({
      nombre: "Alice Barbershop",
      userId: ALICE_UID,
      plan: "VIP_TRIAL"
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("negocios").doc("biz_alice").update({
    nombre: "Hacked by Bob"
  }));
});

test("Subcollection scoping: Alice can manage citas, Bob cannot update Alice's cita", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("negocios").doc("biz_alice").set({
      nombre: "Alice Barbershop",
      userId: ALICE_UID,
      plan: "VIP_TRIAL"
    });
    await context.firestore().collection("negocios").doc("biz_alice").collection("citas").doc("cita_1").set({
      clienteNombre: "Carlos",
      userId: ALICE_UID,
      precio: 500
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("negocios").doc("biz_alice").collection("citas").doc("cita_1").update({
    precio: 600
  }));

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("negocios").doc("biz_alice").collection("citas").doc("cita_1").update({
    precio: 0
  }));
});
