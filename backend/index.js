import express from "express";
import { createServer } from "http";
import connectDB from "./connect.js";
import contractRoutes from "./contractRoutes.js";

const app = express();

const server = createServer(app);

app.use((req, res, next) => {
  res.header("Access-Control-Allow-Credentials", true);
  next();
});
app.use(express.json());
/*app.use(
  cors({
    origin: "http://localhost:3000",
  })
);*/
//app.use(cookieParser());

app.use("/api/contracts", contractRoutes);

connectDB();

const PORT = 8800;
server.listen(PORT, () => {
  console.log(`API working on http://localhost:${PORT}`);
});
