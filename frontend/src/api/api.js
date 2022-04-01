const axios = require("axios");

const testServerUp = function () {
  return axios.get("up");
};

const register = function (data) {
  return axios.post("api/user/create", data);
};

const login = function (data) {
  const config = {
    method: "post",
    url: "login",
    headers: { "Content-Type": "multipart/form-data" },
    data: data,
  };

  return axios(config);
};

const logout = function () {
  return axios("logout");
};

const getCurrentUser = function () {
  return axios.get("api/user");
};

const createGroup = function (groupName) {
  return axios.post("api/group/create", null, {
    params: { groupname: groupName },
  });
};

const getUsers = function () {
  return axios.get("api/group/getUsers");
};

const removeUserFromGroup = function (username) {
  return axios.post("api/group/remove", null, {
    params: { username: username },
  });
};

const addUserToGroup = function (username) {
  return axios.post("api/group/add", null, {
    params: { username: username },
  });
};

const changeAdmin = function (username) {
  return axios.post("api/group/changeAdmin", null, {
    params: { username: username },
  });
};

const usernameExists = function (username) {
  return axios.get("api/user/exists", {
    params: { username: username },
  });
};

const getUserBillsForGroup = function () {
  return axios.get("api/group/getAllUserBills");
};

const getListItemsForGroup = function () {
  return axios.get("api/group/getAllListItems");
};

const payBill = function (billId) {
  return axios.post("api/bill/pay", null, {
    params: { billId: billId },
  });
};

module.exports = {
  testServerUp: testServerUp,
  register: register,
  login: login,
  logout: logout,
  getCurrentUser: getCurrentUser,
  createGroup: createGroup,
  removeUserFromGroup: removeUserFromGroup,
  getUsers: getUsers,
  changeAdmin: changeAdmin,
  usernameExists: usernameExists,
  addUserToGroup: addUserToGroup,
  getUserBillsForGroup: getUserBillsForGroup,
  getListItemsForGroup: getListItemsForGroup,
  payBill: payBill,
};
