import { shallowMount } from "@vue/test-utils";

import setupVue from "../setup";
import Login from "@/views/Login";

describe("Login", () => {
  before(function () {
    // setup mock routes
    setupVue();
  });
  it("should mount Login", () => {
    shallowMount(Login);
  });
});
