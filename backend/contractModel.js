import mongoose from "mongoose";

const ContractSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: "User" },
  abi: { type: String, required: true },
});

const Contract = mongoose.model("Contract", ContractSchema);

export default Contract;
