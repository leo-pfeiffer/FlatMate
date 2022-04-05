import { shallowMount } from "@vue/test-utils";

import setupVue from "../setup";
import Lobby from "@/views/Lobby";

describe("Lobby", () => {
  before(function () {
    setupVue();
  });
  it("should mount", () => {
    shallowMount(Lobby);
  });
});
