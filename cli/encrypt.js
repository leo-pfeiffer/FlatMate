const crypto = require("crypto");

/**
 * Create a hash of the password using a salt
 * @param password - The password to hash.
 * @param salt - The salt to be added to the password.
 * @returns The hash of the password and salt.
 */
const hashWithSalt = function (password, salt) {
  return crypto.createHmac("sha256", password).update(salt).digest("hex");
};

if (process.argv.length !== 4) {
  throw new Error("Usage: node encrypt.js <password> <salt>");
}

const password = process.argv[2];
const salt = process.argv[3];

console.log(hashWithSalt(password, salt));
