<template>
  <div class="login">
    <section class="center-screen">
      <div class="card login-card">
        <div class="card-content">
          <form @submit.prevent="submit">
            <b-field label="Username">
              <b-input type="text" name="username" v-model="form.username" />
            </b-field>
            <b-field label="Password">
              <b-input type="password" name="password" v-model="form.password" />
            </b-field>
            <button class="button" type="submit">Login</button>
          </form>
          <p v-if="showError" class="error-text">Username or Password is incorrect</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { mapActions } from "vuex";

export default {
  name: "Login",
  components: {},
  data() {
    return {
      form: {
        username: "",
        password: ""
      },
      showError: false
    };
  },
  methods: {
    ...mapActions(["LogIn"]),
    async submit() {
      const User = new FormData();
      User.append("username", this.form.username);
      User.append("password", this.form.password);
      try {
        await this.LogIn(User);
        this.$router.push("/");
        this.showError = false;
      } catch (error) {
        this.showError = true;
      }
    }
  }
};
</script>

<style scoped>
</style>
