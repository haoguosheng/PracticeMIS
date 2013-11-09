
                        function tab(step, h1_width, obj, titleClass, divClass, totalWidth) {
                            var titles = obj.getElementsByClassName(titleClass);
                            var divs = obj.getElementsByClassName(divClass);
                            var shouldWidth = step * h1_width;
                            var pos = new Array();
                            var cur = new Array();
                            for (var k = 0; k < titles.length; k++) {
                                var temp = parseInt(k / step);
                                if ((k % step) === 0) {
                                    titles[k].style.backgroundColor = "#FFF";
                                    divs[k].style.display = "block";
                                    divs[k].style.borderWidth = "none";
                                    cur.push(k);
                                    pos.push(temp * shouldWidth);
                                }
                                var real;
                                if (totalWidth - shouldWidth * temp < shouldWidth) {
                                    real = totalWidth - shouldWidth * temp;
                                } else {
                                    real = shouldWidth;
                                }
                                divs[k].style.width = real + "px";
                                divs[k].style.left = pos[temp] + "px";
                            }
                            for (var i = 0; i < titles.length; i++) {
                                titles[i].onmouseover = function() {
                                    var index = 0;
                                    for (var j = 0; j < titles.length; j++) {
                                        if (titles[j] === this) {
                                            index = j;
                                            break;
                                        }
                                    }
                                    var t = parseInt(index / step);
                                    titles[cur[t]].style.backgroundColor = "#F0F6FA";
                                    divs[cur[t]].style.display = "none";
                                    divs[cur[t]].style.borderBottom = "1px solid blue";

                                    titles[index].style.backgroundColor = "#FFF";
                                    divs[index].style.display = "block";
                                    divs[index].style.left = pos[t] + "px";
                                    divs[index].style.borderBottom = "none";
                                    cur[t] = index;
                                };
                            }
                        }
                        var obj = document.getElementById("tab3");
                        tab(6, 65, obj, "tabTitle", "tabChild", 1100);

                        var obj2 = document.getElementById("tab1");
                        tab(6, 65, obj2, "tabTitle", "tabChild", 1100);

                        var obj3 = document.getElementById("tab2");
                        tab(6, 65, obj3, "tabTitle", "tabChild", 1100);