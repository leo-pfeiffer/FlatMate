<template>
  <aside class="menu level">
    <div class="box">
      <div class="buttons is-centered">
        <button class="button is-primary">New Bill</button>
        <button class="button is-primary">New List</button>
      </div>
    </div>
  </aside>
</template>
<script>
export default {
  name: "SideBar"
};
</script>

<style scoped>

.menu {
  display: inline-block;
  vertical-align: top;
  max-height: 100vh;
  overflow-y: auto;
  top: 0;
  bottom: 0;
  padding: 30px;
}
hr {
  margin-top: 10px;
  margin-bottom: 10px;
}

</style>