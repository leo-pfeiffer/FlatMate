<template>
  <aside class="menu level">
    <div class="box">
      <div class="buttons is-centered">
        <button class="button is-primary" @click="newBillModal">
          New Bill
        </button>
        <button class="button is-primary" @click="newListModal">
          New List
        </button>
      </div>
    </div>
  </aside>
</template>
<script>
import CreateBillModal from "@/components/CreateBillModal";
import CreateListModal from "@/components/CreateListModal";
import { getListsForGroup, getUsers } from "@/api/api";

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
  methods: {
    getGroupUsers() {
      return getUsers().then((res) => res.data.users);
    },
    getGroupLists() {
      return getListsForGroup().then((res) => res.data.lists);
    },
    async newBillModal() {
      this.$buefy.modal.open({
        parent: this,
        component: CreateBillModal,
        hasModalCard: true,
        trapFocus: true,
        props: {
          users: await this.getGroupUsers(),
          lists: await this.getGroupLists(),
        },
        events: {
          BillAdded: () => {
            this.$emit("BillAdded");
          },
        },
      });
    },
    newListModal() {
      this.$buefy.modal.open({
        parent: this,
        component: CreateListModal,
        hasModalCard: true,
        trapFocus: true,
        events: {
          ListAdded: () => {
            this.$emit("ListAdded");
          },
        },
      });
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
