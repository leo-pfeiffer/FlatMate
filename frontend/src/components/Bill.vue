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
      <button class="button is-warning" v-if="!paid">Pay</button>
      <button class="button is-success is-disabled" v-else>Paid!</button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "Bill",
  props: {
    id: Number,
    name: String,
    description: String,
    amount: Number,
    percentages: Array,
    owner: String,
    paymentMethod: String,
    paid: Boolean,
  },
  computed: {
    ...mapGetters({User: "StateUser"})
  },

};
</script>

<style scoped>
.user-row {
  font-weight: bolder;
}
</style>
