<template>
  <div id="nav">
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
</template>
<script>
import { mapGetters } from "vuex";

export default {
  name: "NavBar",
  computed: {
    ...mapGetters({ isAdmin: "isAdmin" }),
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
