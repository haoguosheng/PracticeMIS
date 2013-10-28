/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


var oldHint="";
var oldText="";
var searchtype="2";
var oldId="";
var n;
//存放树div的onkeypress事件，定位node结点
function catch_press(){//tree

    var s=String.fromCharCode(event.keyCode);
  
    if(tree.currentNode.id>=1){

        if(s=="0"||s=="1"||s=="2"||s=="3"||s=="4"||s=="5"||s=="6"||s=="7"||s=="8"||s=="9")
        {
            searchtype="2";
            oldId=oldId+s;
        }
        else if(s=="a"||s=="b"||s=="c"||s=="d"||s=="e"||s=="f"||s=="g"||s=="h"||s=="i"||s=="j"||s=="k"||s=="l"||s=="m"||s=="n"||s=="o"||s=="p"||s=="q"||s=="r"||s=="s"||s=="t"||s=="u"||s=="v"||s=="w"||s=="x"||s=="y"||s=="z"||s=="A"||s=="B"||s=="C"||s=="D"||s=="E"||s=="F"||s=="G"||s=="H"||s=="I"||s=="J"||s=="K"||s=="L"||s=="M"||s=="N"||s=="O"||s=="P"||s=="Q"||s=="R"||s=="S"||s=="T"||s=="U"||s=="V"||s=="W"||s=="X"||s=="Y"||s=="Z")
        {
            searchtype="1";
            oldHint=oldHint+s;
        }
        else
        {
            searchtype="0";
            oldText=oldText+s;
        }
       
        event.returnValue = false;
        if (!event.returnValue && this.onchange)
        {
            this.onchange(tree);
        }
    }

    switch(searchtype){
        case "0":
            searchoptiontext(tree);
            break;
        case "1":
            searchoptionhint(tree);
            break;
        case "2":
            searchoptionid(tree);
            break;
    }
    
// selMouseOver();
}

//通过hint描述定位(pinying)
function searchoptionhint(tree){
    if(tree){
        var invalue=oldHint;
        var treelength=tree.totalNode;
        for(var i=1;i<treelength;i++){
            var temp=tree.node[i].hint;
            var tem=temp.toString();
            var tem1=tem.indexOf(",");
            temp=temp.substring(3,tem1);
            if(temp==invalue){
                var index=tree.node[i].sourceIndex.indexOf("_")+1;
                id=tree.node[i].sourceIndex.substring(index,tree.node[i].sourceIndex.length);
                //tree.focusClientNode(id);
                tree.focus(id);
                // tree.currentNode=tree.node[i];
                oldText ="";
                oldHint ="";
                oldId="";
                break;
            }//else{alert("您输入有误，没有该项内容，请重新输入！");break;}
        }
       
    }
}
//通过值text定位
function searchoptiontext(tree){
    if(tree){
        var invalue=oldText;
        var treelength=tree.totalNode;
        for(var i=1;i<treelength;i++){
            var temp=tree.node[i].text;
            if(temp==invalue){
                tree.focus(tree.node[i].id);
                document.getElementById("treeviewarea").fireEvent('onclick');
                break;

            }

        }
    }
}
//通过值id定位
function searchoptionid(tree){
    if(tree){
        var invalue= parseInt(oldId);
        var treelength=tree.totalNode;
        for(var i=1;i<treelength;i++){
            var index=tree.node[i].sourceIndex.indexOf("_")+1;
            id=tree.node[i].sourceIndex.substring(index,tree.node[i].sourceIndex.length);
            var temp=id;
            if(temp==invalue){
                tree.focus(id);
                document.getElementById("treeviewarea").fireEvent('onclick');
                break;
            }

        }

    }
}


//onfocus事件,清空保存的值
function catch_focus(obj) {
    oldText ="";
    oldHint ="";
    oldId="";
}

//onkeydown事件，修改值
function catch_keydown(obj)
{ 
    switch(event.keyCode)
    {
        case 13: //回车键
            catch_focus(obj);
            if(document.getElementById("OKbutton")!= undefined){
                document.getElementById("OKbutton").fireEvent("onclick");
            }
            event.returnValue = false;
            break;
        case 27: //Esc键
            catch_focus(obj);
            event.returnValue = false;
            break;
        case 8:  //空格健
            var s = "";
            switch(searchtype){
                case "0":
                    s=oldText;
                    s = s.substr(0,s.length-1);
                    oldText=s;
                    break;
                case "1":
                    s=oldHint;
                    s = s.substr(0,s.length-1);
                    oldHint=s;
                    break;
                case "2":
                    s=oldId;
                    s = s.substr(0,s.length-1);
                    oldId=s;
                    break;
            }
            event.returnValue = false;
            break;
    }
    if (!event.returnValue && obj.onchange)
        obj.onchange(obj);
}
function dealDbclick(obj){
    catch_focus(obj);
    if(document.getElementById("OKbutton")!= undefined){
        document.getElementById("OKbutton").fireEvent("onclick");
    }
    event.returnValue = false;

}
