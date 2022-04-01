import axios from "axios";

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

const logout = function() {
  return axios("logout");
}

module.exports = {
  register,
  login,
  logout,
};
