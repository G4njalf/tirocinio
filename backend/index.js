import express from "express";
import { createServer } from "http";
import connectDB from "./connect.js";
import contractRoutes from "./contractRoutes.js";
import cors from "cors";

const app = express();

app.use((req, res, next) => {
  res.header("Access-Control-Allow-Credentials", true);
  next();
});
app.use(
  cors({
    origin: "*",
  })
);
//app.use(cookieParser());
app.use(express.json());
app.use("/api/contracts", contractRoutes);

connectDB();

const PORT = 8800;
const server = createServer(app);
server.listen(PORT, "0.0.0.0", () => {
  console.log(`API working on http://localhost:${PORT}`);
});
