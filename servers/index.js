const express= require('express');

const app= express();


const port= process.argv[2] || 3000;

app.get('/', (req, res) => {
        res.send(`Hello from server ${port} !!`);
});

app.get('/healthcheck',(req,res)=>{
    res.sendStatus(200).send();
});

app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});