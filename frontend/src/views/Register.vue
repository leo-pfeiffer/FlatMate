<template>
  <div class="register">
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
            <button class="button" type="submit">Register</button>
          </form>
          <p v-if="showError" class="error-text">Username already exists</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { mapActions } from "vuex";

export default {
  name: "Register",
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
    ...mapActions(["Register"]),
    async submit() {
      try {
        await this.Register(this.form);
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

