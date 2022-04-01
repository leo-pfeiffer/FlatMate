<template>
  <div id="list" class="box">
    <p class="title">{{ name }}</p>
    <p class="subtitle" :class="{ 'user-row': owner === User }">
      <span>Created by: {{ owner }}</span>
    </p>
    <div class="content">
      <p>{{ new Date(time * 1e3).toISOString().slice(0, 10) }}<br /></p>
      <p>{{ description }}</p>
      <ul class="inner-list">
        <li v-for="(item, index) of listItems" :key="index">
          {{ item.name }}
        </li>
      </ul>
      <p class="has-text-info-dark" v-if="billId !== null">Not billed</p>
      <p class="has-text-success-dark" v-else>Bill created</p>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "List",
  props: {
    id: Number,
    time: Number,
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
