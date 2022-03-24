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
                      :title="item.title"
                      :description="item.description"
                      :percentages="item.percentages"
                      :amount="item.amount"
                      :owner="item.owner"
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
                      :owner="item.owner"
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

export default {
  name: "Home",
  components: {
    SideBar,
    Bill,
    List
  },
  data() {
    return {
      bills: [
        {
          id: 1,
          title: "Spotify",
          description: "Monthly music subscription",
          amount: 9.99,
          percentages: [
            { username: "leopold", percentage: 0.5 },
            { username: "lukas", percentage: 0.5 },
          ],
          owner: "leopold",
          paymentMethod: "Cash",
          paid: false,
        },
        {
          id: 2,
          title: "Shopping",
          description: "Grocery run",
          amount: 27.32,
          percentages: [
            { username: "leopold", percentage: 0.5 },
            { username: "lukas", percentage: 0.5 },
          ],
          owner: "lukas",
          paymentMethod: "Cash",
          paid: false,
        },
        {
          id: 3,
          title: "Drinks",
          description: "Night out at the pub",
          amount: 22.1,
          percentages: [
            { username: "leopold", percentage: 0.6 },
            { username: "lukas", percentage: 0.4 },
          ],
          owner: "lukas",
          paymentMethod: "Cash",
          paid: true,
        },
        {
          id: 4,
          title: "Shopping",
          description: "Another grocery run",
          amount: 34.1,
          percentages: [
            { username: "leopold", percentage: 0.6 },
            { username: "lukas", percentage: 0.4 },
          ],
          owner: "lukas",
          paymentMethod: "Cash",
          paid: true,
        },
        {
          id: 5,
          title: "Shopping",
          description: "Another grocery run",
          amount: 34.1,
          percentages: [
            { username: "leopold", percentage: 0.6 },
            { username: "lukas", percentage: 0.4 },
          ],
          owner: "lukas",
          paymentMethod: "Cash",
          paid: true,
        },
        {
          id: 6,
          title: "Shopping",
          description: "Another grocery run",
          amount: 34.1,
          percentages: [
            { username: "leopold", percentage: 0.6 },
            { username: "lukas", percentage: 0.4 },
          ],
          owner: "lukas",
          paymentMethod: "Cash",
          paid: true,
        },
      ],
      lists: [
        {
          id: 1,
          name: "Shopping list 1",
          description: "Gotta eat",
          owner: "lukas",
          listItems: [{name: "Peach"}, {name: "Pear"}, {name: "Plums"}, {name: "Oranges"}],
          billId: null
        },
        {
          id: 2,
          name: "Shopping list 2",
          description: "Always hungry",
          owner: "leopold",
          listItems: [{name: "Peach"}, {name: "Pear"}, {name: "Plums"}, {name: "Oranges"}],
          billId: null,
        },
        {
          id: 3,
          name: "Shopping list 3",
          description: "Gimme gimme food",
          owner: "lukas",
          listItems: [{name: "Peach"}, {name: "Pear"}, {name: "Plums"}, {name: "Oranges"}],
          billId: 1,
        },
      ]
    };
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
