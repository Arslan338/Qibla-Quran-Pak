// summarize uiautomator xml: one line per node with text/desc/id, bounds in dp
const fs=require('fs');const x=fs.readFileSync(process.argv[2],'utf8');
const all=process.argv[3]==='all';
const re=/<node ([^>]*?)\/?>/g;let m;
const attr=(s,k)=>{const r=new RegExp(' '+k+'="([^"]*)"');const a=s.match(r);return a?a[1]:''};
while((m=re.exec(x))){const s=' '+m[1];
 const t=attr(s,'text'),d=attr(s,'content-desc'),id=attr(s,'resource-id').replace(/^.*:id\//,''),c=attr(s,'class').replace(/^.*\./,''),b=attr(s,'bounds');
 const ck=attr(s,'clickable')==='true'?'C':'';const sc=attr(s,'scrollable')==='true'?'S':'';
 if(!all && !t && !d && !id && !ck && !sc) continue;
 const n=b.match(/\d+/g).map(Number);const dp=v=>(v/3.5).toFixed(0);
 console.log(`${c} ${id?'#'+id:''} ${t?'"'+t+'"':''} ${d?'['+d+']':''} ${ck}${sc} px[${n}] dp[${dp(n[0])},${dp(n[1])} ${dp(n[2]-n[0])}x${dp(n[3]-n[1])}]`.replace(/ +/g,' '));}
