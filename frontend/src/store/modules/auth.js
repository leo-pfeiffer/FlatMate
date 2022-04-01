import axios from "axios";
import crypto from "crypto";

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

    await axios.post("api/user/create", config);
    let UserForm = new FormData();
    UserForm.append("username", form.username);
    UserForm.append("password", form.password);
    await dispatch("LogIn", UserForm);
  },

  async LogIn({ commit }, user) {
    // hash password with salt
    user.set("password", hashWithSalt(user.get("password")));

    const config = {
      method: "post",
      url: "login",
      headers: { "Content-Type": "multipart/form-data" },
      data: user,
    };

    // todo cookie?
    const isAdmin = await axios(config).then((res) => {
      return res.data["roles"].indexOf("ADMIN") !== -1; // check if user is admin
    });
    await commit("setUser", user.get("username"));
    await commit("setAdmin", isAdmin); // todo
  },

  async LogOut({ commit }) {
    let user = null;
    // logout from backend
    await axios("logout");
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
