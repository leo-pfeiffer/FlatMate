<template>
  <form action="">
    <div class="modal-card" style="width: auto">
      <header class="modal-card-head">
        <p class="modal-card-title">New bill</p>
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
                {{item}}
              </li>
            </ul>
          </div>
        </div>
      </section>
      <footer class="modal-card-foot">
        <b-button label="Close" @click="$emit('close')" />
        <b-button label="Save" type="is-primary" @click="submitForm" />
      </footer>
    </div>
  </form>
</template>

<script>
export default {
  name: "CreateListModal",
  data() {
    return {
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
    formFilledOut: function() {
      return this.form.name !== "" && this.form.listItems.length !== 0;
    },
    submitForm: function () {
      if (this.formFilledOut()) {
        console.log(this.form);
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
