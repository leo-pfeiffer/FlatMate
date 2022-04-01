const axios = require("axios");

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

const removeUserFromGroup = function(username) {
  return axios.post("api/group/remove", null, {
    params: { username: username }
  })
}

module.exports = {
  register: register,
  login: login,
  logout: logout,
  getCurrentUser: getCurrentUser,
  createGroup: createGroup,
  removeUserFromGroup: removeUserFromGroup,
};
