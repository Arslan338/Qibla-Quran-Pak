// usage: node px.js file.png x,y [x,y ...]   -> hex colors ; or  node px.js file.png row Y x0 x1 step ; col X y0 y1 step
const fs=require('fs'),zlib=require('zlib');
function load(f){const b=fs.readFileSync(f);let p=8,w,h,ct,bd,idat=[];
 while(p<b.length){const len=b.readUInt32BE(p),type=b.toString('ascii',p+4,p+8),d=b.slice(p+8,p+8+len);
  if(type==='IHDR'){w=d.readUInt32BE(0);h=d.readUInt32BE(4);bd=d[8];ct=d[9];}
  if(type==='IDAT')idat.push(d);p+=12+len;}
 const raw=zlib.inflateSync(Buffer.concat(idat));const bpp=ct===6?4:ct===2?3:ct===0?1:4;const st=w*bpp;
 const out=Buffer.alloc(h*st);let prev=Buffer.alloc(st);
 for(let y=0;y<h;y++){const ft=raw[y*(st+1)];const line=raw.slice(y*(st+1)+1,(y+1)*(st+1));const cur=Buffer.alloc(st);
  for(let i=0;i<st;i++){const a=i>=bpp?cur[i-bpp]:0,up=prev[i],c=i>=bpp?prev[i-bpp]:0;let v=line[i];
   if(ft===1)v+=a;else if(ft===2)v+=up;else if(ft===3)v+=((a+up)>>1);else if(ft===4){const pp=a+up-c,pa=Math.abs(pp-a),pb=Math.abs(pp-up),pc=Math.abs(pp-c);v+=(pa<=pb&&pa<=pc)?a:(pb<=pc?up:c);}
   cur[i]=v&255;}
  cur.copy(out,y*st);prev=cur;}
 return {w,h,bpp,get:(x,y)=>{const o=y*st+x*bpp;return [out[o],out[o+1],out[o+2]];}};}
const im=load(process.argv[2]);const hex=c=>'#'+c.map(v=>v.toString(16).padStart(2,'0')).join('').toUpperCase();
const a=process.argv.slice(3);
if(a[0]==='row'){const y=+a[1],x0=+a[2],x1=+a[3],s=+a[4]||20;let o=[];for(let x=x0;x<=x1;x+=s)o.push(x+':'+hex(im.get(x,y)));console.log(o.join(' '));}
else if(a[0]==='col'){const x=+a[1],y0=+a[2],y1=+a[3],s=+a[4]||20;let o=[];for(let y=y0;y<=y1;y+=s)o.push(y+':'+hex(im.get(x,y)));console.log(o.join(' '));}
else if(a[0]==='runs'){ // runs col X y0 y1 : print color change runs
 const x=+a[1],y0=+a[2],y1=+a[3];let last='',st=y0;for(let y=y0;y<=y1;y++){const h=hex(im.get(x,y));if(h!==last){if(last)console.log(st+'-'+(y-1)+' '+last);last=h;st=y;}}console.log(st+'-'+y1+' '+last);}
else if(a[0]==='hruns'){const y=+a[1],x0=+a[2],x1=+a[3];let last='',st=x0;for(let x=x0;x<=x1;x++){const h=hex(im.get(x,y));if(h!==last){if(last)console.log(st+'-'+(x-1)+' '+last);last=h;st=x;}}console.log(st+'-'+x1+' '+last);}
else if(a[0]!=='hist') for(const p of a){const [x,y]=p.split(',').map(Number);console.log(p+' '+hex(im.get(x,y)));}
if(a[0]==='hist'){const [x0,y0,x1,y1]=a.slice(1,5).map(Number);const m={};for(let y=y0;y<y1;y+=1)for(let x=x0;x<x1;x+=1){const h=hex(im.get(x,y));m[h]=(m[h]||0)+1;}
 const tot=(x1-x0)*(y1-y0);console.log(Object.entries(m).sort((p,q)=>q[1]-p[1]).slice(0,+a[5]||8).map(([k,v])=>k+' '+(100*v/tot).toFixed(1)+'%').join(' | '));}
