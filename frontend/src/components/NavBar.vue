<template>
  <div id="nav">
    <div>
      <span v-if="isLoggedIn">
        <router-link to="/">Home</router-link>
        |
        <router-link to="/user">Profile</router-link>
      </span>
      <span v-if="isLoggedIn && isAdmin">
        |
        <router-link to="/admin">Admin</router-link>
      </span>
      <span v-if="isLoggedIn">
        |
        <a @click="logout">Logout</a>
      </span>
      <span v-else>
        <router-link to="/register">Register</router-link> |
        <router-link to="/login">Login</router-link>
      </span>
    </div>
    <div v-if="isLoggedIn">
      <span> ------------------------------ </span>
    </div>
    <div v-if="isLoggedIn">
      <span class="has-text-weight-bold has-text-primary">
        Hello {{ User }}!!
      </span>
    </div>
  </div>
</template>
<script>
import { mapGetters } from "vuex";

export default {
  name: "NavBar",
  computed: {
    ...mapGetters({ isAdmin: "isAdmin", User: "StateUser" }),
    isLoggedIn: function () {
      return this.$store.getters.isAuthenticated;
    },
  },
  methods: {
    async logout() {
      await this.$store.dispatch("LogOut");
      await this.$router.push("/login");
    },
  },
};
</script>
<!--suppress CssUnusedSymbol -->
<style>
#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

a:hover {
  cursor: pointer;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>
