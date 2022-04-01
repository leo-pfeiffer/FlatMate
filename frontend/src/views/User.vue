<template>
  <div id="user" class="columns">
    <UserSideBar id="my-side-bar" class="column" />
    <section id="content-section">
      <div class="columns is-centered">
        <div class="column">
          <div class="card is-center m-4">
            <div class="card-header">
              <div class="card-header-title">Due payments</div>
            </div>
            <div class="card-content">
              <div class="columns is-centered">
                <div>
                  <div>
                    <p class="has-text-centered m-3">
                      <span>You have</span><br />
                      <span class="has-text-weight-bold">{{
                        totalDueAmount.toFixed(2)
                      }}</span
                      ><br />
                      <span>in payments due.</span>
                    </p>
                    <button class="button is-primary" @click="payAll">
                      Pay all due bills
                    </button>
                  </div>
                  <hr />
                  <table class="centered-table">
                    <tr>
                      <th>Name</th>
                      <th>Total Amount</th>
                      <th>Your share</th>
                      <th></th>
                    </tr>
                    <tr v-for="item in duePayments" :key="item.id">
                      <td>{{ item.name }}</td>
                      <td>{{ item.amount.toFixed(2) }}</td>
                      <td>{{ item.percentage }}</td>
                      <td>
                        <b-icon icon="xmark" size="is-small" type="is-danger" />
                      </td>
                    </tr>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="column">
          <div class="card is-center m-4">
            <div class="card-header">
              <div class="card-header-title">Total payments</div>
            </div>
            <div class="card-content">
              <p class="has-text-centered m-4">
                <span>In total, you've paid</span><br />
                <span class="has-text-weight-bold">{{
                  totalPaymentAmount.toFixed(2)
                }}</span
                ><br />
                <span>since joining this group.</span>
              </p>
            </div>
          </div>

          <div class="card is-center m-4">
            <div class="card-header">
              <div class="card-header-title">History</div>
            </div>
            <div class="card-content">
              <div class="columns is-centered">
                <table class="centered-table">
                  <tr>
                    <th>Name</th>
                    <th>Total Amount</th>
                    <th>Your share</th>
                    <th></th>
                  </tr>
                  <tr v-for="item in history" :key="item.id">
                    <td>{{ item.name }}</td>
                    <td>{{ item.amount.toFixed(2) }}</td>
                    <td>{{ item.percentage }}</td>
                    <td>
                      <b-icon icon="check" size="is-small" type="is-success" />
                    </td>
                  </tr>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import UserSideBar from "@/components/UserSideBar";
import { getUserBillsForGroup, payBill } from "@/api/api";
import { mapGetters } from "vuex";

export default {
  name: "User",
  components: {
    UserSideBar,
  },
  data() {
    return {
      history: [
        { id: 1, name: "food", amount: 30, percentage: 0.5 },
        { id: 2, name: "beer", amount: 41, percentage: 0.5 },
        { id: 3, name: "electricity", amount: 30.1, percentage: 0.5 },
        { id: 4, name: "water", amount: 27.2, percentage: 0.5 },
        { id: 5, name: "netflix", amount: 9.99, percentage: 0.5 },
      ],
      duePayments: [
        { id: 6, name: "spotify", amount: 9.99, percentage: 0.5 },
        { id: 7, name: "dinner", amount: 18.3, percentage: 0.5 },
        { id: 8, name: "food", amount: 15.34, percentage: 0.5 },
      ],
    };
  },
  computed: {
    ...mapGetters({ User: "StateUser" }),
    totalPaymentAmount: function () {
      return this.history.reduce(
        (agg, next) => next.amount * next.percentage + agg,
        0
      );
    },
    totalDueAmount: function () {
      return this.duePayments.reduce(
        (agg, next) => next.amount * next.percentage + agg,
        0
      );
    },
  },
  mounted() {
    this.setHistory();
    this.setDuePayments();
  },
  methods: {
    payAll: async function () {
      for (let dueBill of this.duePayments) {
        await payBill(dueBill.id);
      }
      await this.setHistory();
      await this.setDuePayments();
    },
    getAllUserBills: function () {
      return getUserBillsForGroup()
        .then((res) => res.data)
        .then((userBills) => {
          const myBills = [];
          for (let ub of userBills) {
            if (ub.user.username === this.User) {
              myBills.push({
                id: ub.bill.billId,
                name: ub.bill.name,
                amount: ub.bill.amount,
                percentage: ub.percentage,
                paid: ub.paid,
              });
            }
          }
          return myBills;
        });
    },
    setHistory: async function () {
      const userBills = await this.getAllUserBills();
      this.history = userBills.filter((e) => e.paid);
    },
    setDuePayments: async function () {
      const userBills = await this.getAllUserBills();
      this.duePayments = userBills.filter((e) => !e.paid);
    },
  },
};
</script>

<!--suppress CssUnusedSymbol -->
<style scoped>
#user {
  height: 100%;
}

#my-side-bar {
  min-width: 250px;
}

#content-section {
  width: 100%;
}

#content-section,
.is-mobile {
  margin-left: 30px;
  margin-right: 30px;
}
</style>
