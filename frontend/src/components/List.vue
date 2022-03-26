<template>
  <div id="list" class="box">
    <p class="title">{{ name }}</p>
    <p class="subtitle" :class="{ 'user-row': owner === User }">
      <span>Created by: {{ owner }}</span>
    </p>
    <div class="content">
      <p>{{ description }}</p>
      <ul class="inner-list">
        <li v-for="(item, index) of listItems" :key="index">
          {{ item.name }}
        </li>
      </ul>
      <button class="button is-warning" v-if="billId !== null">
        Create bill
      </button>
      <button class="button is-warning" v-else>Go to bill</button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "List",
  props: {
    id: Number,
    name: String,
    description: String,
    owner: String,
    listItems: Array,
    billId: Number,
  },
  computed: {
    ...mapGetters({ User: "StateUser" }),
  },
};
</script>

<style scoped>
.user-row {
  font-weight: bolder;
}

.inner-list {
  text-align: left;
}
</style>
