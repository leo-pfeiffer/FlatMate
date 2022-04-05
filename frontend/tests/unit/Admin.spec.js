import { shallowMount } from "@vue/test-utils";
import setupVue from "../setup";
import Admin from "@/views/Admin";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";

let mock;

const setupMock = function () {
  mock = new MockAdapter(axios);

  mock.onGet("api/group/getUsers").reply(200, {
    users: [{ username: "leopold" }, { username: "lukas" }],
  });
};

describe("Admin", () => {
  before(function () {
    setupVue();
  });
  beforeEach(function () {
    setupMock();
  });
  after(function () {
    mock.restore();
  });
  it("should mount", () => {
    shallowMount(Admin);
  });
});
