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

            <b-field label="Amount*">
              <b-numberinput
                step="0.01"
                aria-minus-label="Decrement by 0.01"
                aria-plus-label="Increment by 0.01"
                min="0"
                v-model="form.amount"
              ></b-numberinput>
            </b-field>

            <b-field label="Payment Method">
              <b-input
                type="text"
                placeholder="Cash, PayPal, ..."
                v-model="form.paymentMethod"
              ></b-input>
            </b-field>
          </div>

          <div class="column">
            <b-field label="Add to list">
              <b-select
                placeholder="Select list"
                expanded
                v-model="form.listId"
              >
                <option v-for="list in lists" :value="list.id" :key="list.id">
                  {{ list.name }}
                </option>
              </b-select>
            </b-field>

            <b-field label="Contributors">
              <b-select
                multiple
                native-size="6"
                v-model="form.selectedUsers"
                @input="updatePercentageMap"
                expanded
              >
                <option v-for="u in users" :value="u" :key="u">
                  {{ u }}
                </option>
              </b-select>
            </b-field>
          </div>

          <div class="column" v-if="form.selectedUsers.length > 0">
            <div>
              <span class="has-text-weight-bold">Contribution share</span><br>
              <span class="has-text-danger" v-if="percentageSum !== 1">
                Sum: {{ percentageSum.toFixed(2) }}
              </span>
              <span v-else class="has-text-success">
                Percentages match up!
              </span>
            </div>

            <b-field
              :label="u"
              v-for="u in form.selectedUsers"
              :value="u"
              :key="u"
            >
              <b-numberinput
                step="0.01"
                min="0"
                max="1"
                aria-minus-label="Decrement by 0.01"
                aria-plus-label="Increment by 0.01"
                @input="(value) => updatePercentages(value, u)"
              ></b-numberinput>
            </b-field>
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
  name: "CreateBillModal",
  props: {
    users: Array,
    lists: Array,
  },
  data() {
    return {
      percentageSum: 0,
      form: {
        name: "",
        description: "",
        amount: 0,
        paymentMethod: "",
        listId: "",
        selectedUsers: [],
        selectedPercentages: {},
      },
    };
  },
  methods: {
    updatePercentages: function (value, user) {
      this.form.selectedPercentages[user] = value;
      this.percentageSum = this.getPercentagesSum();
    },
    updatePercentageMap: function () {
      const existingKeys = [...Object.keys(this.form.selectedPercentages)];
      existingKeys.forEach((k) => {
        if (this.form.selectedUsers.indexOf(k) === -1) {
          delete this.form.selectedPercentages[k];
        }
      });
      this.percentageSum = this.getPercentagesSum();
    },
    getPercentagesSum: function () {
      return Object.values(this.form.selectedPercentages).reduce(
        (a, b) => a + b,
        0
      );
    },
    formFilledOut: function () {
      return (
        this.form.name !== "" &&
        this.form.amount > 0 &&
        this.form.selectedUsers.length > 0 &&
        this.percentageSum === 1 &&
        this.form.selectedUsers
          .map((e) => e in this.form.selectedPercentages)
          .reduce((a, b) => a && b, true)
      );
    },
    submitForm: function () {
      if (this.formFilledOut()) {
        console.log(this.form);
      }
    },
  },
};
</script>

<style scoped></style>
