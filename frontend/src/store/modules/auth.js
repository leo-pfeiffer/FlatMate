import crypto from "crypto";
import { login, logout, register } from "@/api/api";

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
    // salt and hash password
    const config = {
      username: form.username,
      password: hashWithSalt(form.password),
    };

    await register(config);
    let UserForm = new FormData();
    UserForm.append("username", form.username);
    UserForm.append("password", form.password);
    await dispatch("LogIn", UserForm);
  },

  async LogIn({ commit }, user) {
    // hash password with salt
    user.set("password", hashWithSalt(user.get("password")));

    // todo cookie?
    const isAdmin = await login(user).then((res) => {
      return res.data["roles"].indexOf("ADMIN") !== -1; // check if user is admin
    });
    await commit("setUser", user.get("username"));
    await commit("setAdmin", isAdmin); // todo
  },

  async LogOut({ commit }) {
    let user = null;
    // logout from backend
    await logout();
    // remove user object from store
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

const hashWithSalt = function (password) {
  const SALT = process.env.VUE_APP_SALT;
  return crypto.createHmac("sha256", password).update(SALT).digest("hex");
};

export default {
  state,
  getters,
  actions,
  mutations,
};
