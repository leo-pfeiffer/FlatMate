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

          <div class="box columns is-centered" v-if="searched">
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

export default {
  name: "Admin",
  data() {
    return {
      users: [
        { username: "leopold" },
        { username: "lukas" },
        { username: "lucas" },
        { username: "jonathan" },
      ],
      searchInput: "",
      searchResult: "",
      noResults: false,
      searched: false,
    };
  },
  computed: {
    ...mapGetters({ User: "StateUser" }),
  },
  methods: {
    search: function () {
      // todo
      if (this.searchInput === 'error') {
        this.noResults = true;
      } else {
        this.searchResult = this.searchInput;
        this.noResults = false;
      }
      this.searched = true;
    },
    setSearched: function(val) {
      this.searched = val;
      this.searchResult = "";
      this.noResults = false;
    },
    makeAdmin: function(username) {
      // todo
      console.log("New admin", username);
    },
    removeUser: function(username) {
      console.log("Removed", username);
    },
    addUser: function() {
      console.log("New user", this.searchResult)
    }
  },
};
</script>

<style scoped>
.centered-table {
  border-collapse: separate;
  border-spacing: 10px;
}
.centered-table td {
  margin: 8px 8px 8px 8px;
  padding: 8px 8px 8px 8px;
  vertical-align: middle;
}
</style>
