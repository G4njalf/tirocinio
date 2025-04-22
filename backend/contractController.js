import Contract from "./contractModel.js";

export const getContract = async (req, res) => {
  console.log("request get contract");
  console.log(req.body);
};

export const postContract = async (req, res) => {
  console.log("request post contract");
  console.log(req.body);
};

export const updateContract = async (req, res) => {
  console.log("request update contract");
  console.log(req.body);
};

export const deleteContract = async (req, res) => {
  console.log("request delete contract");
  console.log(req.body);
};
