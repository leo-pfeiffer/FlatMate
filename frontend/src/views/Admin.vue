<template>
  <div id="admin" class="container">
    <div class="columns is-centered">
      <div class="card is-center">
        <div class="card-header">
          <div class="card-header-title">Manage group members</div>
        </div>
        <div class="card-content">
          <b-field label="Add member">
            <b-input
              placeholder="username"
              expanded
              v-model="searchInput"
              @input="() => setSearched(false)"
            >
            </b-input>
            <p class="control">
              <b-button type="is-primary" label="Search" @click="search" />
            </p>
          </b-field>

          <div class="box columns is-centered m-4" v-if="searched">
            <table class="centered-table" v-if="!noResults">
              <tr>
                <td>
                  <span>{{ searchResult }}</span>
                </td>
                <td>
                  <b-button type="is-primary" label="Add" @click="addUser" />
                </td>
              </tr>
            </table>
            <div v-else>
              <p class="has-text-danger">No users found.</p>
            </div>
          </div>

          <hr />

          <div class="columns is-centered">
            <table class="centered-table">
              <tr v-for="user in users" :key="user.username">
                <td :class="{ 'has-text-weight-bold': user.username !== User }">
                  {{ user.username }}
                </td>
                <td>
                  <b-button
                    class="is-primary"
                    v-bind="{ disabled: user.username === User }"
                    @click="() => removeUser(user.username)"
                  >
                    Remove
                  </b-button>
                </td>
                <td>
                  <b-button
                    class="is-primary"
                    v-bind="{ disabled: user.username === User }"
                    @click="() => makeAdmin(user.username)"
                  >
                    Make admin
                  </b-button>
                </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import {
  changeAdmin,
  getUsers,
  removeUserFromGroup,
  usernameExists,
} from "@/api/api";

export default {
  name: "Admin",
  data() {
    return {
      users: [],
      searchInput: "",
      searchResult: "",
      noResults: false,
      searched: false,
    };
  },
  computed: {
    ...mapGetters({ User: "StateUser" }),
  },
  async mounted() {
    this.users = await getUsers()
      .then((res) => {
        return res.data.users;
      })
      .then((users) =>
        users.map((u) => {
          return { username: u };
        })
      );
  },
  methods: {
    search: async function () {
      if (this.searchInput.length > 0) {
        const res = await usernameExists(this.searchInput).then(
          (res) => res.data
        );

        if (!res) {
          this.noResults = true;
        } else {
          this.searchResult = this.searchInput;
          this.noResults = false;
        }
        this.searched = true;
      }
    },
    setSearched: function (val) {
      this.searched = val;
      this.searchResult = "";
      this.noResults = false;
    },
    makeAdmin: async function (username) {
      console.log("New admin", username);
      await changeAdmin(username);
      await this.$store.dispatch("AdminRole");
      await this.$router.push("/");
      this.removeUserFromList(username);
    },
    removeUser: async function (username) {
      console.log("Removed", username);
      await removeUserFromGroup(username);
      this.removeUserFromList(username);
    },
    addUser: function () {
      console.log("New user", this.searchResult);
    },
    removeUserFromList: function (username) {
      let index = -1;
      for (let i = 0; i < this.users.length; i++) {
        if (this.users[i].username === username) {
          index = i;
        }
      }
      this.users.splice(index, 1);
    },
  },
};
</script>

<style scoped></style>
