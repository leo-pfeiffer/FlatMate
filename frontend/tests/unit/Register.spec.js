import { shallowMount } from "@vue/test-utils";

import setupVue from "../setup";
import Register from "@/views/Register";

describe("Register", () => {
  before(function () {
    setupVue();
  });
  it("should mount", () => {
    shallowMount(Register);
  });
});
