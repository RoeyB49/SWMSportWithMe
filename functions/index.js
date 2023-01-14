// The Cloud Functions for Firebase SDK
// to create Cloud Functions and set up triggers.
const functions = require("firebase-functions");
// The Firebase Admin SDK to access Firestore.
const admin = require("firebase-admin");
admin.initializeApp();

exports.createUser = functions.https.onCall((data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError(
        "failed-precondition",
        "The function must be called while authenticated."
    );
  }
  return admin.auth().createUser({
    email: data.email,
    password: data.password,
  })
      .then((userRecord) => {
        return {userRecord};
      })
      .catch((error) => {
        throw new functions.https.HttpsError(error.code, error.message);
      });
});
