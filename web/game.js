(() => {
  'use strict';

  const canvas = document.getElementById('game');
  const ctx = canvas.getContext('2d');
  const overlay = document.getElementById('startOverlay');
  const startButton = document.getElementById('startButton');
  const levelLabel = document.getElementById('levelLabel');

  const W = canvas.width, H = canvas.height;
  const TILE = 64;
  const COLS = 15, ROWS = 15;
  const MAPS = {
    1: [
      [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],[1,1,1,1,1,1,1,0,0,0,0,0,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,1,0,0,0,1,1,0,0,1,1,1,1],[1,1,1,0,0,0,0,1,1,0,0,1,1,1,1],[1,1,1,1,1,1,0,0,0,0,0,0,0,0,1],[1,1,1,1,1,1,1,0,0,0,0,0,0,1,1],[1,1,1,1,1,1,1,0,0,0,0,1,1,1,1],[1,1,1,1,1,1,0,0,0,0,1,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,0,1,1,1],[1,1,1,1,0,0,0,0,0,0,0,1,1,1,1],[1,1,0,0,0,0,0,1,1,1,1,1,1,1,1],[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
    ],
    2: [
      [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],[1,1,1,1,1,1,1,0,0,0,0,0,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,1,0,0,0,1,1,0,0,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,1,1,1,0,0,0,0,0,0,0,0,1],[1,1,1,1,1,1,1,0,0,0,0,0,0,1,1],[1,1,1,1,1,1,1,0,0,0,0,1,1,1,1],[1,1,1,1,1,1,0,0,0,0,1,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,0,1,1,1],[1,1,1,1,0,0,0,0,0,0,0,1,1,1,1],[1,1,0,0,0,0,0,1,1,1,1,1,1,1,1],[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
    ],
    3: [
      [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],[1,1,1,1,1,1,1,0,0,0,0,0,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,1,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,1,1,1,0,0,0,0,0,0,0,0,1],[1,1,1,1,1,1,1,0,0,0,0,0,0,1,1],[1,1,1,1,1,1,1,0,0,0,0,1,1,1,1],[1,1,1,1,1,1,0,0,0,0,1,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,1,1,1,1],[1,1,1,0,0,0,0,0,0,0,0,0,1,1,1],[1,1,1,1,0,0,0,0,0,0,0,1,1,1,1],[1,1,0,0,0,0,0,1,1,1,1,1,1,1,1],[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
    ]
  };

  const LEVELS = {
    1: { spawn:[9,3], objects:[['Key',3,5],['NextLevelDoor',2,13]] },
    2: { spawn:[9,3], objects:[['Key',3,5],['NextLevelDoor',2,13]] },
    3: { spawn:[9,3], objects:[['Key',3,5],['NextLevelDoor',2,13]] }
  };

  const images = {};
  const imagePaths = {
    earth:'res/tiles/earth.png', wall:'res/tiles/wall.png', key:'res/objects/key.png', door:'res/objects/door.png', boots:'res/objects/boots.png', chest:'res/objects/chest.png',
    up1:'res/player/boy_up_1.png', up2:'res/player/boy_up_2.png', down1:'res/player/boy_down_1.png', down2:'res/player/boy_down_2.png', left1:'res/player/boy_left_1.png', left2:'res/player/boy_left_2.png', right1:'res/player/boy_right_1.png', right2:'res/player/boy_right_2.png',
    zombie:'res/monsters/zombie.png', skeleton:'res/monsters/skeleton.png', katana:'res/weapons/katana.png'
  };
  let loadedImages = 0;
  for (const [name, src] of Object.entries(imagePaths)) {
    const img = new Image(); img.src = src; img.onload = img.onerror = () => loadedImages++;
    images[name] = img;
  }

  const audio = {
    music: new Audio('res/sound/BlueBoyAdventure.wav'),
    coin: new Audio('res/sound/coin.wav'), fanfare: new Audio('res/sound/fanfare.wav'), powerup: new Audio('res/sound/powerup.wav'), unlock: new Audio('res/sound/unlock.wav')
  };
  audio.music.loop = true; audio.music.volume = .35;
  for (const [k,a] of Object.entries(audio)) if (k !== 'music') a.volume = .55;
  function playSfx(name) { const a = audio[name]; if (!a) return; try { a.currentTime = 0; a.play(); } catch {} }

  const keys = new Set();
  const mouse = { x: W/2, y:H/2, down:false };
  let started = false, last = performance.now(), message='', messageUntil=0, gameFinished=false;
  let zoom = 1;
  const damageNumbers = [];
  const trails = [];

  const player = { x:9*TILE, y:3*TILE, w:24, h:32, hitOffX:20, hitOffY:24, speed: 3.55, speedMultiplier:1, keys:0, facing:'down', frame:1, animTime:0, angle:0, attacking:false, attackStart:0, attackDuration:260, attackIndex:0, hitSet:new Set() };
  let currentLevel = 1, objects=[], monsters=[], dummy=null;

  function setupLevel(level) {
    if (!LEVELS[level]) { showMessage('No more levels are included in this JAR.'); return false; }
    currentLevel = level; levelLabel.textContent = String(level); gameFinished = false;
    const d = LEVELS[level]; player.x = d.spawn[0]*TILE; player.y = d.spawn[1]*TILE;
    objects = d.objects.map(([type,x,y]) => ({type,x:x*TILE,y:y*TILE,collision:type.includes('Door'),removed:false}));
    dummy = { x:9*TILE, y:11*TILE, health:100, maxHealth:100, flashUntil:0 };
    monsters = [makeZombie(5*TILE,11*TILE), makeZombie(7*TILE,7*TILE)];
    return true;
  }

  function makeZombie(x,y) { return { x,y,w:48,h:48,health:50,maxHealth:50,speed:2,alive:true,flashUntil:0,path:[],pathIndex:0,pathCooldown:0 }; }
  function showMessage(text, ms=2000) { message=text; messageUntil=performance.now()+ms; }

  function rectsOverlap(a,b) { return a.x < b.x+b.w && a.x+a.w > b.x && a.y < b.y+b.h && a.y+a.h > b.y; }
  function playerRectAt(x=player.x,y=player.y) { return {x:x+20,y:y+24,w:24,h:32}; }
  function entityRect(e,x=e.x,y=e.y) { return {x:x+8,y:y+8,w:TILE-16,h:TILE-16}; }
  function tileBlockedAtRect(r) {
    const map = MAPS[currentLevel];
    const minC = Math.floor(r.x/TILE), maxC = Math.floor((r.x+r.w-1)/TILE);
    const minR = Math.floor(r.y/TILE), maxR = Math.floor((r.y+r.h-1)/TILE);
    for (let row=minR; row<=maxR; row++) for (let col=minC; col<=maxC; col++) if (row<0||col<0||row>=ROWS||col>=COLS||map[row][col]===1) return true;
    return false;
  }
  function objectBlockedAtRect(r, ignore=null) { return objects.some(o => !o.removed && o!==ignore && o.collision && rectsOverlap(r,{x:o.x,y:o.y,w:TILE,h:TILE})); }
  function monsterBlockedAtRect(r, self=null) { return monsters.some(m => m!==self && m.alive && rectsOverlap(r,entityRect(m))); }

  function tryMovePlayer(dx,dy) {
    if (!dx && !dy) return;
    const nx=player.x+dx, ny=player.y+dy, r=playerRectAt(nx,ny);
    if (!tileBlockedAtRect(r) && !objectBlockedAtRect(r) && !monsterBlockedAtRect(r)) { player.x=nx; player.y=ny; }
    checkInteractions();
  }

  function checkInteractions() {
    const pr = playerRectAt();
    for (const o of objects) {
      if (o.removed || !rectsOverlap(pr,{x:o.x,y:o.y,w:TILE,h:TILE})) continue;
      if (o.type==='Key') { o.removed=true; player.keys++; playSfx('coin'); showMessage('You got a key!'); }
      else if (o.type==='NextLevelDoor') {
        if (player.keys>0) {
          player.keys--; o.removed=true; playSfx('unlock');
          if (!setupLevel(currentLevel+1)) { player.keys++; o.removed=false; }
        } else showMessage('You need a key to enter!');
      } else if (o.type==='PrevLevelDoor') { playSfx('unlock'); setupLevel(Math.max(1,currentLevel-1)); }
      else if (o.type==='Boots') { o.removed=true; player.speedMultiplier=1.25; playSfx('powerup'); }
      else if (o.type==='Chest') { gameFinished=true; audio.music.pause(); playSfx('fanfare'); }
    }
  }

  function gridPos(x,y) { return [Math.floor((x+TILE/2)/TILE), Math.floor((y+TILE/2)/TILE)]; }
  function astar(sc,sr,gc,gr) {
    if (sc===gc&&sr===gr) return [[sc,sr]];
    const map=MAPS[currentLevel], open=[{c:sc,r:sr,g:0,f:Math.abs(gc-sc)+Math.abs(gr-sr),p:null}], seen=new Map();
    const key=(c,r)=>c+','+r; seen.set(key(sc,sr),0);
    while(open.length){ open.sort((a,b)=>a.f-b.f); const n=open.shift(); if(n.c===gc&&n.r===gr){ const p=[]; for(let q=n;q;q=q.p)p.push([q.c,q.r]); return p.reverse(); }
      for(const [dc,dr] of [[1,0],[-1,0],[0,1],[0,-1]]){ const c=n.c+dc,r=n.r+dr; if(c<0||r<0||c>=COLS||r>=ROWS||map[r][c]===1)continue; const g=n.g+1,k=key(c,r); if(seen.has(k)&&seen.get(k)<=g)continue; seen.set(k,g); open.push({c,r,g,f:g+Math.abs(gc-c)+Math.abs(gr-r),p:n}); }
    } return [];
  }

  function updateMonsters(dt) {
    const px=player.x+TILE/2, py=player.y+TILE/2;
    for(const m of monsters){ if(!m.alive)continue; const mx=m.x+TILE/2,my=m.y+TILE/2; const dist=Math.hypot(px-mx,py-my); if(dist>5*TILE)continue;
      m.pathCooldown-=dt; if(m.pathCooldown<=0){ const [sc,sr]=gridPos(m.x,m.y), [gc,gr]=gridPos(player.x,player.y); m.path=astar(sc,sr,gc,gr); m.pathIndex=1; m.pathCooldown=250; }
      let tx=px,ty=py; if(m.path[m.pathIndex]){ tx=m.path[m.pathIndex][0]*TILE+TILE/2; ty=m.path[m.pathIndex][1]*TILE+TILE/2; if(Math.hypot(tx-mx,ty-my)<9)m.pathIndex++; }
      const dx=tx-(m.x+TILE/2), dy=ty-(m.y+TILE/2), len=Math.hypot(dx,dy)||1; const step=m.speed*(dt/(1000/120)); let vx=dx/len*step,vy=dy/len*step;
      const nr=entityRect(m,m.x+vx,m.y+vy); if(!tileBlockedAtRect(nr)&&!objectBlockedAtRect(nr)&&!monsterBlockedAtRect(nr,m)&&!rectsOverlap(nr,playerRectAt())){m.x+=vx;m.y+=vy;} else { const rx=entityRect(m,m.x+vx,m.y); if(!tileBlockedAtRect(rx)&&!objectBlockedAtRect(rx)&&!monsterBlockedAtRect(rx,m)&&!rectsOverlap(rx,playerRectAt()))m.x+=vx; const ry=entityRect(m,m.x,m.y+vy); if(!tileBlockedAtRect(ry)&&!objectBlockedAtRect(ry)&&!monsterBlockedAtRect(ry,m)&&!rectsOverlap(ry,playerRectAt()))m.y+=vy; }
    }
  }

  const attacks=[{dur:260,start:-45,end:45,reach:1.1},{dur:260,start:45,end:-45,reach:1.1},{dur:380,start:-80,end:80,reach:1.2}];
  function beginAttack(now){ if(player.attacking)return; const a=attacks[player.attackIndex%3]; player.attacking=true; player.attackStart=now; player.attackDuration=a.dur; player.swingBase=player.angle; player.hitSet.clear(); player.attackIndex=(player.attackIndex+1)%3; }
  function attackPose(now){ if(!player.attacking)return null; const a=attacks[(player.attackIndex+2)%3]; const t=Math.min(1,(now-player.attackStart)/a.dur); if(t>=1){player.attacking=false;return null;} const smooth=t*t*(3-2*t); const angle=player.swingBase+(a.start+(a.end-a.start)*smooth)*Math.PI/180; const reach=TILE*(.30 + .08*Math.sin(Math.PI*t)); const handX=player.x+TILE/2+Math.cos(angle)*reach, handY=player.y+TILE/2+Math.sin(angle)*reach; const bladeLen=48*a.reach; const tipX=handX+Math.cos(angle)*bladeLen,tipY=handY+Math.sin(angle)*bladeLen; return {angle,handX,handY,tipX,tipY,t}; }
  function pointSegmentDistance(px,py,x1,y1,x2,y2){ const dx=x2-x1,dy=y2-y1,l2=dx*dx+dy*dy;if(!l2)return Math.hypot(px-x1,py-y1);let t=((px-x1)*dx+(py-y1)*dy)/l2;t=Math.max(0,Math.min(1,t));return Math.hypot(px-(x1+t*dx),py-(y1+t*dy)); }
  function hitCombat(pose,now){ if(!pose || pose.t<.15 || pose.t>.85)return; const hit=(target,id,damage)=>{ if(player.hitSet.has(id))return; const cx=target.x+TILE/2,cy=target.y+TILE/2;if(pointSegmentDistance(cx,cy,pose.handX,pose.handY,pose.tipX,pose.tipY)<34){player.hitSet.add(id);target.health-=damage;target.flashUntil=now+150;damageNumbers.push({n:damage,x:cx,y:target.y,t:now});if(target.health<=0&&'alive'in target)target.alive=false;} };
    monsters.forEach((m,i)=>{if(m.alive)hit(m,'m'+i,10)}); if(dummy)hit(dummy,'dummy',10);
  }

  function update(dt,now) {
    if(gameFinished)return;
    let dx=0,dy=0; if(keys.has('w')||keys.has('arrowup')){dy=-1;player.facing='up';} else if(keys.has('s')||keys.has('arrowdown')){dy=1;player.facing='down';} else if(keys.has('d')||keys.has('arrowright')){dx=1;player.facing='right';} else if(keys.has('a')||keys.has('arrowleft')){dx=-1;player.facing='left';}
    const scale=dt/(1000/120), speed=player.speed*player.speedMultiplier*scale; if(dx||dy){ tryMovePlayer(dx*speed,dy*speed); player.animTime+=dt; if(player.animTime>125){player.frame=player.frame===1?2:1;player.animTime=0;} }
    const centerX=W/2,centerY=H/2; player.angle=Math.atan2(mouse.y-centerY,mouse.x-centerX); if(mouse.down){ beginAttack(now); const deg=player.angle*180/Math.PI; player.facing=deg>=-45&&deg<45?'right':deg>=45&&deg<135?'down':deg>=-135&&deg<-45?'up':'left'; }
    const pose=attackPose(now); hitCombat(pose,now); if(pose) trails.push({x1:pose.handX,y1:pose.handY,x2:pose.tipX,y2:pose.tipY,t:now});
    while(trails.length&&now-trails[0].t>140)trails.shift(); while(damageNumbers.length&&now-damageNumbers[0].t>900)damageNumbers.shift();
    updateMonsters(dt); zoom = keys.has('v') ? 0.62 : 1;
  }

  function drawImageSafe(img,x,y,w,h){ if(img&&img.complete&&img.naturalWidth)ctx.drawImage(img,x,y,w,h); }
  function worldToScreen(x,y){ return [(x-player.x + W/2)*zoom, (y-player.y + H/2)*zoom]; }
  function drawWorld(now){
    ctx.save(); ctx.translate(W/2,H/2); ctx.scale(zoom,zoom); ctx.translate(-W/2,-H/2);
    const camX=player.x-W/2,camY=player.y-H/2, map=MAPS[currentLevel];
    for(let r=0;r<ROWS;r++)for(let c=0;c<COLS;c++){ const x=c*TILE-camX,y=r*TILE-camY; drawImageSafe(map[r][c]===1?images.wall:images.earth,x,y,TILE,TILE); }
    for(const o of objects){ if(o.removed)continue; const x=o.x-camX,y=o.y-camY; drawImageSafe(o.type==='Key'?images.key:o.type.includes('Door')?images.door:o.type==='Boots'?images.boots:images.chest,x,y,TILE,TILE); }
    if(dummy){ const x=dummy.x-camX,y=dummy.y-camY; ctx.save(); if(now<dummy.flashUntil)ctx.globalAlpha=.45; ctx.fillStyle='#b78055';ctx.fillRect(x+18,y+8,28,48);ctx.fillStyle='#d8b38a';ctx.beginPath();ctx.arc(x+32,y+12,11,0,Math.PI*2);ctx.fill();ctx.restore(); drawBar(x,y-10,dummy.health,dummy.maxHealth); }
    for(const m of monsters){ if(!m.alive)continue; const x=m.x-camX,y=m.y-camY; ctx.save(); if(now<m.flashUntil)ctx.globalAlpha=.45; drawImageSafe(images.zombie,x,y,TILE,TILE);ctx.restore(); drawBar(x,y-10,m.health,m.maxHealth); }
    // sword trail in world coordinates
    ctx.lineCap='round'; for(const tr of trails){ const age=(now-tr.t)/140; ctx.strokeStyle=`rgba(235,245,255,${(1-age)*.62})`;ctx.lineWidth=8*(1-age)+2;ctx.beginPath();ctx.moveTo(tr.x1-camX,tr.y1-camY);ctx.lineTo(tr.x2-camX,tr.y2-camY);ctx.stroke(); }
    // player
    const pImg=images[player.facing+(player.frame===2?'2':'1')]; drawImageSafe(pImg,W/2,H/2,TILE,TILE);
    const pose=attackPose(now); if(pose){ ctx.save(); const hx=pose.handX-camX,hy=pose.handY-camY;ctx.translate(hx,hy);ctx.rotate(pose.angle+Math.PI/2);drawImageSafe(images.katana,-12,-42,24,64);ctx.restore(); }
    for(const d of damageNumbers){ const age=(now-d.t)/900;ctx.globalAlpha=1-age;ctx.fillStyle='#fff';ctx.strokeStyle='#111';ctx.lineWidth=4;ctx.font='bold 25px Arial';const x=d.x-camX,y=d.y-camY-age*45;ctx.strokeText(String(d.n),x,y);ctx.fillText(String(d.n),x,y);ctx.globalAlpha=1; }
    ctx.restore();
  }
  function drawBar(x,y,hp,max){ ctx.fillStyle='#292929';ctx.fillRect(x,y,TILE,6);ctx.fillStyle='#d52d2d';ctx.fillRect(x,y,TILE*Math.max(0,hp)/max,6);ctx.strokeStyle='#000';ctx.strokeRect(x,y,TILE,6); }
  function drawUI(now){
    drawImageSafe(images.key,32,32,TILE,TILE);ctx.fillStyle='#fff';ctx.font='40px Arial';ctx.fillText('x '+player.keys,95,80);
    ctx.font='22px Arial';ctx.fillStyle='#ffffffb8';ctx.fillText('Level '+currentLevel,32,H-30);
    if(now<messageUntil){ctx.fillStyle='#fff';ctx.font='30px Arial';ctx.textAlign='center';ctx.fillText(message,W/2,300);ctx.textAlign='left';}
    if(gameFinished){ctx.fillStyle='#0009';ctx.fillRect(0,0,W,H);ctx.fillStyle='#fff';ctx.font='48px Arial';ctx.textAlign='center';ctx.fillText('You Found the Treasure!',W/2,H/2);ctx.textAlign='left';}
  }
  function render(now){ ctx.clearRect(0,0,W,H);ctx.fillStyle='#000';ctx.fillRect(0,0,W,H);drawWorld(now);drawUI(now); }
  function loop(now){ const dt=Math.min(40,now-last);last=now;if(started)update(dt,now);render(now);requestAnimationFrame(loop); }

  function canvasMouse(e){ const r=canvas.getBoundingClientRect(); mouse.x=(e.clientX-r.left)*W/r.width;mouse.y=(e.clientY-r.top)*H/r.height; }
  addEventListener('keydown',e=>{ keys.add(e.key.toLowerCase()); if(['arrowup','arrowdown','arrowleft','arrowright',' '].includes(e.key.toLowerCase()))e.preventDefault(); });
  addEventListener('keyup',e=>keys.delete(e.key.toLowerCase()));
  canvas.addEventListener('mousemove',canvasMouse);canvas.addEventListener('mousedown',e=>{if(e.button===0){canvasMouse(e);mouse.down=true;canvas.focus();}});addEventListener('mouseup',e=>{if(e.button===0)mouse.down=false;});
  canvas.addEventListener('contextmenu',e=>e.preventDefault());
  startButton.addEventListener('click',()=>{started=true;overlay.classList.add('hidden');canvas.focus();audio.music.play().catch(()=>{});});

  setupLevel(1); requestAnimationFrame(loop);
})();
