import express from "express";
import {
  getContract,
  postContract,
  updateContract,
  deleteContract,
} from "./contractController.js";

const router = express.Router();

router.get("/getContract", getContract);
router.post("/postContract", postContract);
router.put("/postContract", updateContract);
router.delete("/postContract", deleteContract);

export default router;
