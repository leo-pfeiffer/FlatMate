<template>
  <div id="lobby" class="container center-screen">
    <div class="card" id="lobby-card">
      <div class="card-content is-center">
        <div class="columns">
          <div class="column">
            <p>
              You are not part of a group yet.<br />
              To use <span class="has-text-weight-bold">Flat Mate</span>, either
              create your own group, or ask an admin to add you.
            </p>
          </div>

          <hr class="is-mobile" />

          <div class="column">
            <b-field label="Create new group">
              <b-input placeholder="group name" expanded v-model="groupName">
              </b-input>
              <p class="control">
                <b-button
                  type="is-primary"
                  label="Create"
                  @click="createGroup"
                  :disabled="clicked"
                />
              </p>
            </b-field>
            <p class="error-text" v-if="isError">Group name already exists.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { createGroup } from "@/api/api";

export default {
  name: "Admin",
  data() {
    return {
      groupName: "",
      clicked: false,
      isError: false,
    };
  },
  methods: {
    createGroup: async function () {
      if (this.groupName.length > 0) {
        try {
          const res = await createGroup(this.groupName);
          if (res === undefined) throw Error("Group exists");
          await this.$store.dispatch("CurrentGroup");
          await this.$store.dispatch("AdminRole");
          await this.$router.push("/");
          this.isError = false;
        } catch (error) {
          this.isError = true;
          this.clicked = false;
        }
      }
    },
  },
};
</script>

<style scoped>
@media (min-width: 600px) {
  #lobby-card {
    width: 550px;
  }
}
</style>
