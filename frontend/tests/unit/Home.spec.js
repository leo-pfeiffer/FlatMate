import { expect } from "chai";
import axios from "axios";
import MockAdapter from "axios-mock-adapter";
import { testServerUp } from "@/api/api";
import { shallowMount } from "@vue/test-utils";
import Home from "@/views/Home";
import setupVue from "../setup";

let mock;

const setupMock = function () {
  mock = new MockAdapter(axios);
  mock.onGet("up").reply(200, "The server is up.");

  mock.onGet("api/group/getAllListItems").reply(200, [
    {
      listItemId: 1,
      name: "peach",
      list: {
        listId: 1,
        name: "shopping list",
        description: "my shopping list",
        owner: {
          username: "lukas",
          password: null,
          group: {
            groupId: 1,
            name: "macintosh",
          },
          role: "USER",
          enabled: true,
        },
        bill: {
          billId: 3,
          name: "shopping",
          description: "grocery run",
          amount: 17.03,
          paymentMethod: "cash",
          owner: {
            username: "lukas",
            password: null,
            group: {
              groupId: 1,
              name: "macintosh",
            },
            role: "USER",
            enabled: true,
          },
          createTime: 1648727500,
        },
        createTime: 1648727500,
        billId: 3,
        listItems: [
          {
            name: "peach",
          },
          {
            name: "pears",
          },
          {
            name: "plums",
          },
          {
            name: "oranges",
          },
        ],
      },
    },
    {
      listItemId: 2,
      name: "pears",
      list: {
        listId: 1,
        name: "shopping list",
        description: "my shopping list",
        owner: {
          username: "lukas",
          password: null,
          group: {
            groupId: 1,
            name: "macintosh",
          },
          role: "USER",
          enabled: true,
        },
        bill: {
          billId: 3,
          name: "shopping",
          description: "grocery run",
          amount: 17.03,
          paymentMethod: "cash",
          owner: {
            username: "lukas",
            password: null,
            group: {
              groupId: 1,
              name: "macintosh",
            },
            role: "USER",
            enabled: true,
          },
          createTime: 1648727500,
        },
        createTime: 1648727500,
        billId: 3,
      },
    },
    {
      listItemId: 3,
      name: "plums",
      list: {
        listId: 1,
        name: "shopping list",
        description: "my shopping list",
        owner: {
          username: "lukas",
          password: null,
          group: {
            groupId: 1,
            name: "macintosh",
          },
          role: "USER",
          enabled: true,
        },
        bill: {
          billId: 3,
          name: "shopping",
          description: "grocery run",
          amount: 17.03,
          paymentMethod: "cash",
          owner: {
            username: "lukas",
            password: null,
            group: {
              groupId: 1,
              name: "macintosh",
            },
            role: "USER",
            enabled: true,
          },
          createTime: 1648727500,
        },
        createTime: 1648727500,
        billId: 3,
      },
    },
    {
      listItemId: 4,
      name: "oranges",
      list: {
        listId: 1,
        name: "shopping list",
        description: "my shopping list",
        owner: {
          username: "lukas",
          password: null,
          group: {
            groupId: 1,
            name: "macintosh",
          },
          role: "USER",
          enabled: true,
        },
        bill: {
          billId: 3,
          name: "shopping",
          description: "grocery run",
          amount: 17.03,
          paymentMethod: "cash",
          owner: {
            username: "lukas",
            password: null,
            group: {
              groupId: 1,
              name: "macintosh",
            },
            role: "USER",
            enabled: true,
          },
          createTime: 1648727500,
        },
        createTime: 1648727500,
        billId: 3,
      },
    },
  ]);

  mock.onGet("api/group/getAllUserBills").reply(200, [
    {
      userBillId: 5,
      user: {
        username: "leopold",
        password: null,
        group: null,
        role: "USER",
        enabled: true,
      },
      bill: {
        billId: 2,
        name: "netflix",
        description: "monthly streaming service",
        amount: 9.99,
        paymentMethod: "paypal",
        owner: {
          username: "lukas",
          password: null,
          group: {
            groupId: 1,
            name: "macintosh",
          },
          role: "USER",
          enabled: true,
        },
        createTime: 1648727500,
        percentages: [
          {
            username: "leopold",
            percentage: 0.33,
            paid: true,
          },
          {
            username: "lukas",
            percentage: 0.33,
            paid: false,
          },
          {
            username: "lucas",
            percentage: 0.34,
            paid: false,
          },
        ],
      },
      percentage: 0.33,
      paid: true,
    },
    {
      userBillId: 6,
      user: {
        username: "lukas",
        password: null,
        group: {
          groupId: 1,
          name: "macintosh",
        },
        role: "USER",
        enabled: true,
      },
      bill: {
        billId: 2,
        name: "netflix",
        description: "monthly streaming service",
        amount: 9.99,
        paymentMethod: "paypal",
        owner: {
          username: "lukas",
          password: null,
          group: {
            groupId: 1,
            name: "macintosh",
          },
          role: "USER",
          enabled: true,
        },
        createTime: 1648727500,
      },
      percentage: 0.33,
      paid: false,
    },
    {
      userBillId: 7,
      user: {
        username: "lucas",
        password: null,
        group: null,
        role: "USER",
        enabled: true,
      },
      bill: {
        billId: 2,
        name: "netflix",
        description: "monthly streaming service",
        amount: 9.99,
        paymentMethod: "paypal",
        owner: {
          username: "lukas",
          password: null,
          group: {
            groupId: 1,
            name: "macintosh",
          },
          role: "USER",
          enabled: true,
        },
        createTime: 1648727500,
      },
      percentage: 0.34,
      paid: false,
    },
    {
      userBillId: 8,
      user: {
        username: "leopold",
        password: null,
        group: null,
        role: "USER",
        enabled: true,
      },
      bill: {
        billId: 3,
        name: "shopping",
        description: "grocery run",
        amount: 17.03,
        paymentMethod: "cash",
        owner: {
          username: "lukas",
          password: null,
          group: {
            groupId: 1,
            name: "macintosh",
          },
          role: "USER",
          enabled: true,
        },
        createTime: 1648727500,
        percentages: [
          {
            username: "leopold",
            percentage: 0.5,
            paid: true,
          },
          {
            username: "lukas",
            percentage: 0.5,
            paid: true,
          },
        ],
      },
      percentage: 0.5,
      paid: true,
    },
    {
      userBillId: 9,
      user: {
        username: "lukas",
        password: null,
        group: {
          groupId: 1,
          name: "macintosh",
        },
        role: "USER",
        enabled: true,
      },
      bill: {
        billId: 3,
        name: "shopping",
        description: "grocery run",
        amount: 17.03,
        paymentMethod: "cash",
        owner: {
          username: "lukas",
          password: null,
          group: {
            groupId: 1,
            name: "macintosh",
          },
          role: "USER",
          enabled: true,
        },
        createTime: 1648727500,
      },
      percentage: 0.5,
      paid: true,
    },
  ]);
};

describe("Home", () => {
  before(function () {
    setupVue();
  });

  beforeEach(function () {
    setupMock();
  });

  after(function () {
    mock.restore();
  });

  it("should succeed the setup test", async () => {
    const result = await testServerUp().then((res) => res.data);
    expect(result).to.equal("The server is up.");
  });

  it("should mount", () => {
    shallowMount(Home);
  });
});
