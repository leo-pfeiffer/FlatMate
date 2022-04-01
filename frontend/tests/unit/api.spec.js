import { expect } from "chai";
import axios from "axios";
import MockAdapter from "axios-mock-adapter";
import { testServerUp } from "@/api/api";

const setupMock = function () {
  const mock = new MockAdapter(axios);
  mock.onGet("up")
    .reply(200, "The server is up.");
};

describe("Test", () => {
  before(function () {
    // setup mock routes
    setupMock();
  });

  it("should work", async () => {
    const result = await testServerUp().then((res) => res.data);
    expect(result).to.equal("The server is up.");
  });
});
