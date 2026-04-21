
module.exports = {
  coverageProvider: "v8",
  rootDir: "dist-test",
  setupFilesAfterEnv: ["./jest-setup.js"],
  testEnvironment: "jsdom",
  testMatch: ["**/*_spec.js"],
  transform: {},
};
