<template>
  <form action="">
    <div class="modal-card" style="width: auto">
      <header class="modal-card-head">
        <p class="modal-card-title">New list</p>
        <button type="button" class="delete" @click="$emit('close')" />
      </header>
      <section class="modal-card-body">
        <div class="container columns">
          <div class="column">
            <b-field label="Name*">
              <b-input
                type="text"
                placeholder="Name"
                required
                v-model="form.name"
              ></b-input>
            </b-field>

            <b-field label="Description">
              <b-input
                type="text"
                placeholder="Description"
                v-model="form.description"
              ></b-input>
            </b-field>
          </div>

          <div class="column">
            <b-field label="Add item">
              <b-input
                type="text"
                placeholder="..."
                v-model="currentItem"
              ></b-input>
            </b-field>
            <b-button label="Add" @click="addItem" />
          </div>

          <div class="column" v-if="this.form.listItems.length > 0">
            <div class="has-text-weight-bold">
              <span>List items</span>
            </div>
            <ul class="bullets">
              <li v-for="(item, index) in form.listItems" :key="index">
                {{ item }}
              </li>
            </ul>
          </div>
        </div>
      </section>
      <footer class="modal-card-foot">
        <b-button label="Close" @click="$emit('close')" />
        <b-button label="Save" type="is-primary" @click="submitForm" />
        <p v-if="created" class="has-text-success-dark">List added!</p>
      </footer>
    </div>
  </form>
</template>

<script>
import { createList, createListItem } from "@/api/api";

export default {
  name: "CreateListModal",
  data() {
    return {
      created: false,
      currentItem: "",
      form: {
        name: "",
        description: "",
        listItems: [],
      },
    };
  },
  methods: {
    addItem() {
      if (this.currentItem !== "") {
        this.form.listItems.push(this.currentItem);
        this.currentItem = "";
      }
    },
    formFilledOut: function () {
      return this.form.name !== "" && this.form.listItems.length !== 0;
    },
    submitForm: async function () {
      if (this.formFilledOut()) {
        const list = {
          name: this.form.name,
          description: this.form.description,
        };

        await createList(list)
          .then((res) => res.data)
          .then((createdList) => {
            return this.form.listItems.map((e) => {
              return {
                name: e,
                list: createdList,
              };
            });
          })
          .then(async (listItems) => {
            for (let li of listItems) {
              await createListItem(li);
            }
          })
          .then(() => {
            this.created = true;
            this.$emit("ListAdded");
          });
      }
    },
  },
};
</script>

<style scoped>
ul.bullets {
  list-style-type: circle;
}
</style>
