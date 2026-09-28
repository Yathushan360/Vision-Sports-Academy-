async function guard(){const r=await fetch('/api/auth/me');const d=await r.json();if(!d.authenticated){location.href='/login';return null}const u=document.getElementById('currentUser');if(u)u.textContent=d.username;return d}
async function logout(){await fetch('/api/auth/logout',{method:'POST'});location.href='/login'}
document.addEventListener('DOMContentLoaded',()=>{guard();const l=document.getElementById('logout');if(l)l.onclick=logout});
async function loadCounts(){try{const [p,pa,c]=await Promise.all([fetch('/api/players'),fetch('/api/parents'),fetch('/api/coaches')]);document.getElementById('playerCount').textContent=(await p.json()).length;document.getElementById('parentCount').textContent=(await pa.json()).length;document.getElementById('coachCount').textContent=(await c.json()).length}catch(e){}}
function statusPill(s){return `<span class="pill ${String(s).toLowerCase()}">${s}</span>`}
