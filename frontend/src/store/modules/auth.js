import axios from "axios";

// built with the help of
// https://www.smashingmagazine.com/2020/10/authentication-in-vue-js/

const state = {
  user: null,
  admin: false, // admin or user
};

const getters = {
  isAuthenticated: (state) => !!state.user,
  StateUser: (state) => state.user,
  isAdmin: (state) => state.admin,
};

const actions = {
  async Register({ dispatch }, form) {
    await axios.post("register", form);
    let UserForm = new FormData();
    UserForm.append("username", form.username);
    UserForm.append("password", form.password);
    await dispatch("LogIn", UserForm);
  },

  async LogIn({ commit }, user) {

    const config = {
      method: "post",
      url: "login",
      headers: { "Content-Type": "multipart/form-data" },
      data: user,
    };

    await axios(config);
    await commit("setUser", user.get("username"));
    await commit("setAdmin", user.get("username") === "leopold"); // todo
  },

  async LogOut({ commit }) {
    let user = null;
    commit("logout", user);
  },
};

const mutations = {
  setUser(state, username) {
    state.user = username;
  },
  setAdmin(state, isAdmin) {
    state.admin = isAdmin;
  },
  logout(state, user) {
    state.user = user;
  },
};

export default {
  state,
  getters,
  actions,
  mutations,
};
