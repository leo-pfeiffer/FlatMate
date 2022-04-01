<template>
  <aside class="menu level">
    <div class="box">
      <p class="subtitle">Moving out?</p>
      <div class="buttons is-centered">
        <button class="button is-primary" @click="leaveGroup">
          Leave group
        </button>
      </div>
    </div>
  </aside>
</template>
<script>
import { mapGetters } from "vuex";
import { removeCurrentUserFromGroup } from "@/api/api";

export default {
  name: "SideBar",
  data() {
    return {
      formProps: {
        email: "evan@you.com",
        password: "testing",
      },
    };
  },
  computed: {
    ...mapGetters({ User: "StateUser" }),
  },
  methods: {
    async leaveGroup() {
      await removeCurrentUserFromGroup(this.User)
        .then(() => this.$store.dispatch("CurrentGroup"))
        .then(() => this.$store.dispatch("AdminRole"))
        .then(() => this.$router.push("/lobby"));
    },
  },
};
</script>

<style scoped>
.menu {
  display: inline-block;
  vertical-align: top;
  max-height: 100vh;
  overflow-y: auto;
  top: 0;
  bottom: 0;
  padding: 30px;
}

hr {
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
