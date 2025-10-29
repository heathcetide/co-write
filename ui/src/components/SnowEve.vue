<template>
  <div class="snow">
    <img src="../assets/snow1.png">
    <img src="../assets/snow3.png">
    <img src="../assets/snow4.png">
    <img src="../assets/snow1.png">
    <img src="../assets/snow3.png">
    <img src="../assets/snow4.png">
    <img src="../assets/snow1.png">
    <img src="../assets/snow3.png">
    <img src="../assets/snow4.png">
    <img src="../assets/snow1.png">
    <img src="../assets/snow3.png">
    <img src="../assets/snow4.png">
  </div>
</template>

<script>
export default {

}
</script>

<style scoped>
.snow{
    position: absolute;
    width: 100%;
    height: 100vh;
    overflow: hidden;
    z-index: 1;
    pointer-events: none;
}
.snow img{
    position: absolute;
}

@keyframes animate{
    0%{
        opacity: 0; top: -10px;
        transform: translateX(20px) rotate(0deg);
    }
    10%{
        opacity: 1;
    }
    20%{
        transform: translateX(-20px) rotate(45deg);
    }
    40%{
        transform: translateX(-20px) rotate(90deg);
    }
    60%{
        transform: translateX(20px) rotate(180deg);
    }
    80%{
        transform: translateX(-20px) rotate(45deg);
    }
    100%{
        top: 110%; transform: translateX(20px) rotate(225deg);
    }
}
.snow img:nth-child(1){
    left: 20%;
    animation: animate 20s linear infinite;
}
.snow img:nth-child(2){
    left: 50%;
    animation: animate 14s -2s linear infinite;
}
.snow img:nth-child(3){
    left: 44%;
    animation: animate 12s -3s linear infinite;
}
.snow img:nth-child(4){
    left: 35%;
    animation: animate 19s -2s linear infinite;
}
.snow img:nth-child(5){
    left: 56%;
    animation: animate 28s -1s linear infinite;
}
.snow img:nth-child(6){
    left: 90%;
    animation: animate 14s -2s linear infinite;
}
.snow img:nth-child(7){
    left: 80%;
    animation: animate 15s -1s linear infinite;
}
.snow img:nth-child(8){
    left: 15%;
    animation: animate 10s -3s linear infinite;
}
.snow img:nth-child(9){
    left: 56%;
    animation: animate 16s -3s linear infinite;
}
.snow img:nth-child(10){
    left: 98%;
    animation: animate 15s -1s linear infinite;
}
.snow img:nth-child(11){
    left: 77%;
    animation: animate 10s -3s linear infinite;
}
.snow img:nth-child(12){
    left: 15%;
    animation: animate 16s -2s linear infinite;
}

</style>