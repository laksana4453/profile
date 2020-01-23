$(document).ready(function () {
    document.getElementById('pro-image').addEventListener('change', readImage, false);

    $(".preview-images-zone").sortable();

    $(document).on('click', '.image-cancel', function () {
        let no = $(this).data('no');
        $(".preview-image.preview-show-" + no).remove();
    });
});

var num = 4;
var arrayURL = [];
function readImage() {
    if (window.File && window.FileList && window.FileReader) {
        var files = event.target.files; //FileList object
        var output = $(".preview-images-zone");
        var arrayfile = [];

        for (let i = 0; i < files.length; i++) {

            var file = files[i];
            if (!file.type.match('image')) continue;
            arrayfile.push(file);

            var picReader = new FileReader();

            picReader.addEventListener('load', function (event) {
                var picFile = event.target;
                var html = '<div class="preview-image preview-show-' + num + '">' +
                    '<div class="image-cancel" data-no="' + num + '">x</div>' +
                    '<div class="image-zone"><img id="pro-img-' + num + '" src="' + picFile.result + '"></div>' +
                    '<div class="tools-edit-image"><a href="javascript:void(0)" data-no="' + num + '" class="btn btn-light btn-edit-image">edit</a></div>' +
                    '</div>';

                output.append(html);
                num = num + 1;
            });

            picReader.readAsDataURL(file);
        }

        var fileCatcher = document.getElementById("file-catcher");
        fileCatcher.addEventListener('submit', function (event) {
            event.preventDefault();
            arrayfile.forEach(function (file) {
                uplodeImageFirebase(file, num)
            });
        });


        $("#pro-image").val('');

    } else {
        console.log('Browser not support');
    }
}
function uplodeImageFirebase(file, num) {

    var uploader = document.getElementById('uploader');
   

    var storageRef = firebase.storage().ref('me/' + file.name);
    var uploadTask = storageRef.put(file);
    uploadTask.on('state_changed', function (snapshot) {
        // Observe state change events such as progress, pause, and resume
        // Get task progress, including the number of bytes uploaded and the total number of bytes to be uploaded
        var progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
        uploader.value = progress;
        console.log('Upload is ' + progress + '% done');
        switch (snapshot.state) {
            case firebase.storage.TaskState.PAUSED: // or 'paused'
                console.log('Upload is paused');
                break;
            case firebase.storage.TaskState.RUNNING: // or 'running'
                console.log('Upload is running');
                break;
        }
    }, function (error) {
        // Handle unsuccessful uploads
    }, function () {

        uploadTask.snapshot.ref.getDownloadURL().then(function (downloadURL) {
            // user defined length
                arrayURL.push(downloadURL) ;
        });
        covertToJson(arrayURL);


    });


}
function covertToJson(arrayurl) {
   
   
    console.log(arrayurl.valueOf());
    
    var textJson = "";
    var show = document.getElementById("textJson");
    var name = document.getElementById("exampleInputName").value;
    var typesize = document.getElementById("typesize").value;
    var size = document.getElementById("size").value;
    var face = document.getElementById("face").value;
    var address = document.getElementById("exampleInputLocaion").value;
    var building = document.getElementById("building").alt;
    var light_bulb = document.getElementById("light_bulb").alt;
    var drop = document.getElementById("drop").alt;
    var landFor =document.getElementById("landFor").value;
    var detailForm = document.getElementById("detailForm").value;
    var seaLavel = document.getElementById("seaLavel").value;
    var transfer_terms = document.getElementById("transfer_terms").value;
    
    console.log(building);


    //rai
    var exampleInputPrice1 = document.getElementById("exampleInputPrice1").value;
    //square_yard
    var exampleInputPrice2 = document.getElementById("exampleInputPrice2").value;
    //all
    var exampleInputPrice3 = document.getElementById("exampleInputPrice3").value;


    textJson += '{';
    textJson += '"area": {' + ' "size": "' + size + '",' + ' "type": "' + typesize + '",' +
        '"wide_face": ' + face + '  },';
    textJson += ' "price": {"square_yard": ' + exampleInputPrice2 + ',"rai": ' + exampleInputPrice1 + ',"all": ' + exampleInputPrice3 + ' },';
    textJson += '"public_utilities":';

    if(building == "building_click"){
        textJson += '{ "building": true,';
    }else{
        textJson += '{ "building": false,';
    }
    if(drop == "drop_click"){
        textJson += '"water": true,';
    }else{
        textJson += '"water": false,';
    }
    if(light_bulb == "light_bulb_click"){
        textJson +='"electric": true},'
    }else{
        textJson +='"electric": false},'
    }
   
    textJson += '"img_link": [';
  
    textJson += "'"+ arrayurl.valueOf()+"'";
     
    textJson += '],';
    textJson += '"_id": "0",';
    textJson += '"land_name": "' + name + '",';
    textJson += '"address": "' + address + '",' +
        ' "land_for": "'+landFor+'",' +
        ' "details": "'+ detailForm+'",'+
        '"sea_level": '+seaLavel+',' +
        '"transfer_terms": "'+transfer_terms+'",' +
        ' "location": [],' + ' "__v": 0,' + '"approved": true'
        ;
    textJson += '}';
    show.innerHTML = textJson;


   
}

