<template>
  <div id="bill" class="box">
    <p class="title">{{ name }}</p>
    <p class="subtitle">
      <span>Amount: {{ amount }}</span
      ><br />
      <span>Created by: {{ owner }}</span
      ><br />
    </p>
    <div class="content">
      <p>{{ (new Date(time * 1e3)).toISOString().slice(0, 10) }}<br></p>
      <p>{{ description }}</p>
      <table>
        <tr
          v-for="item of percentages"
          :key="item.username"
          :class="{ 'user-row': item.username === User }"
        >
          <td>{{ item.username }}</td>
          <td>{{ (item.percentage * amount).toFixed(2) }}</td>
        </tr>
      </table>
      <button class="button is-warning" v-if="!isPaid" @click="payBill(id)">
        Pay
      </button>
      <button class="button is-success is-disabled" v-else>Paid!</button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { payBill } from "@/api/api";

export default {
  name: "Bill",
  props: {
    id: Number,
    time: Number,
    name: String,
    description: String,
    amount: Number,
    percentages: Array,
    owner: String,
    paymentMethod: String,
  },
  computed: {
    ...mapGetters({ User: "StateUser" }),
    isPaid: function () {
      for (let p of this.percentages) {
        if (p.username === this.User) {
          return p.paid;
        }
      }
      return false;
    },
  },
  methods: {
    payBill: async function (billId) {
      await payBill(billId);
      this.setPaid(true);
    },
    setPaid: function (paid) {
      for (let p of this.percentages) {
        if (p.username === this.User) {
          p.paid = paid;
        }
      }
    },
  },
};
</script>

<style scoped>
.user-row {
  font-weight: bolder;
}
</style>
