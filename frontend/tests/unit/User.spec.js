import { createLocalVue, shallowMount } from "@vue/test-utils";
import setupVue from "../setup";
import Vuex from "vuex";
import store from "@/store/index";
import User from "@/views/User";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";

let mock;

const localVue = createLocalVue();

localVue.use(Vuex);

const setupMock = function () {
  mock = new MockAdapter(axios);
  mock.onGet("api/group/getAllUserBills").reply(200, []);
};

describe("User", () => {
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
    shallowMount(User, { store, localVue });
  });
});
