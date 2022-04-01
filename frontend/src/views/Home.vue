<template>
  <div id="home" class="columns">
    <SideBar id="my-side-bar" class="column" />
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
                      :name="item.name"
                      :description="item.description"
                      :percentages="item.percentages"
                      :amount="item.amount"
                      :owner="item.owner.username"
                      :payment-method="item.paymentMethod"
                      :paid="item.paid"
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
                      :id="item.id"
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
      lists: [
        {
          id: 1,
          name: "Shopping list 1",
          description: "Gotta eat",
          owner: "lukas",
          listItems: [
            { name: "Peach" },
            { name: "Pear" },
            { name: "Plums" },
            { name: "Oranges" },
          ],
          billId: null,
        },
      ],
    };
  },
  async mounted() {
    this.bills = await this.getBills();
    this.lists = await this.getLists();
  },
  methods: {
    getBills: async function () {
      const userBills = await getUserBillsForGroup().then((res) => res.data);
      const bills = {};

      for (let ub of userBills) {
        let billId = ub.bill.billId;
        const bill = ub.bill;
        if (!(billId in bills)) {
          bill["percentages"] = [
            {
              username: ub.user.username,
              percentage: ub.percentage,
            },
          ];
          bills[billId] = bill;
        } else {
          bills[billId]["percentages"].push({
            username: ub.user.username,
            percentage: ub.percentage,
          });
        }
      }
      return Object.keys(bills).map(e => bills[e]);
    },
    getLists: async function () {
      const listItems = await getListItemsForGroup().then(res => res.data)
      const lists = {};

      for (let li of listItems) {
        let listId = li.list.listId;
        const list = li.list;
        if (!(listId in lists)) {
          list['listItems'] = [{
            name: li.name
          }]
          lists[listId] = list;
        } else {
          lists[listId]['listItems'].push({
            name: li.name
          });
        }
      }

      return Object.keys(lists).map(e => lists[e])
    },
  },
};
</script>

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

#bill-tiles,
#list-tiles {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
  align-items: center;
}
</style>
