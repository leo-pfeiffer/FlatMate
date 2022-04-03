<template>
  <div id="home" class="columns">
    <SideBar
      id="my-side-bar"
      class="column"
      @ListAdded="updateLists"
      @BillAdded="updateBills"
    />
    <section id="content-section">
      <div class="container">
        <b-tabs>
          <b-tab-item label="Bills" active>
            <div class="container">
              <div class="tile is-ancestor" id="bill-tiles">
                <div
                  class="tile is-parent is-3 bill-item"
                  v-for="(item, index) of bills"
                  :key="index"
                >
                  <article class="tile is-child">
                    <Bill
                      class="bill"
                      :id="item.billId"
                      :name="item.name"
                      :time="item.createTime"
                      :description="item.description"
                      :percentages="item.percentages"
                      :amount="item.amount"
                      :owner="item.owner.username"
                      :payment-method="item.paymentMethod"
                    />
                  </article>
                </div>
              </div>
            </div>
          </b-tab-item>
          <b-tab-item label="Lists">
            <div class="container">
              <div class="tile is-ancestor" id="list-tiles">
                <div
                  class="tile is-parent is-3 bill-item"
                  v-for="(item, index) of lists"
                  :key="index"
                >
                  <article class="tile is-child">
                    <List
                      :id="item.listId"
                      :time="item.createTime"
                      :name="item.name"
                      :description="item.description"
                      :owner="item.owner.username"
                      :list-items="item.listItems"
                      :bill-id="item.billId"
                    />
                  </article>
                </div>
              </div>
            </div>
          </b-tab-item>
        </b-tabs>
      </div>
    </section>
  </div>
</template>
<script>
import SideBar from "@/components/SideBar";
import Bill from "@/components/Bill";
import List from "@/components/List";
import { getListItemsForGroup, getUserBillsForGroup } from "@/api/api";

export default {
  name: "Home",
  components: {
    SideBar,
    Bill,
    List,
  },
  data() {
    return {
      bills: [],
      lists: [],
    };
  },
  mounted() {
    this.updateBills();
    this.updateLists();
  },
  methods: {
    updateLists: async function () {
      this.lists = await this.getLists();
    },
    updateBills: async function () {
      this.bills = await this.getBills();
    },
    getBills: async function () {
      const userBills = await getUserBillsForGroup().then((res) => res.data);
      const bills = {};

      if (userBills[0]["bill"] === undefined) {
        await this.$store.dispatch("LogOut");
        await this.$router.push("/login");
        return;
      }

      for (let ub of userBills) {
        let billId = ub.bill.billId;
        const bill = ub.bill;
        if (!(billId in bills)) {
          bill["percentages"] = [
            {
              username: ub.user.username,
              percentage: ub.percentage,
              paid: ub.paid,
            },
          ];
          bills[billId] = bill;
        } else {
          bills[billId]["percentages"].push({
            username: ub.user.username,
            percentage: ub.percentage,
            paid: ub.paid,
          });
        }
      }
      return Object.keys(bills).map((e) => bills[e]);
    },
    getLists: async function () {
      const listItems = await getListItemsForGroup().then((res) => res.data);
      const lists = {};

      if (listItems[0]["list"] === undefined) {
        await this.$store.dispatch("LogOut");
        await this.$router.push("/login");
        return;
      }

      for (let li of listItems) {
        let listId = li.list.listId;
        const list = li.list;
        if (list.bill !== null) {
          list["billId"] = list.bill.billId;
        } else {
          list["billId"] = null;
        }
        if (!(listId in lists)) {
          list["listItems"] = [
            {
              name: li.name,
            },
          ];
          lists[listId] = list;
        } else {
          lists[listId]["listItems"].push({
            name: li.name,
          });
        }
      }
      return Object.keys(lists).map((e) => lists[e]);
    },
  },
};
</script>

<!--suppress CssUnusedSymbol -->
<style scoped>
#home {
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

.bill-item {
  min-width: 300px;
  max-width: 300px;
}

.bill {
  height: 100%;
}

#bill-tiles,
#list-tiles {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
  align-items: stretch;
}
</style>
