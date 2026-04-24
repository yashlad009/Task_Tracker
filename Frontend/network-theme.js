(() => {
  const reducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  const isSmallScreen = window.innerWidth < 900;
  if (reducedMotion || isSmallScreen) return;

  const canvas = document.createElement('canvas');
  canvas.className = 'theme-network-canvas';
  canvas.setAttribute('aria-hidden', 'true');
  document.body.prepend(canvas);
  const ctx = canvas.getContext('2d');
  if (!ctx) return;
  const nodes = [];
  const pointer = { x: window.innerWidth * 0.5, y: window.innerHeight * 0.5 };
  const NODE_COUNT = Math.min(34, Math.max(18, Math.floor(window.innerWidth / 48)));
  const LINK_DISTANCE = 180, CURSOR_INFLUENCE = 140;
  const STAR_CROSS_SIZE = 1.6, WAVE_SPEED = 0.0021, WAVE_LENGTH = 0.036, WAVE_MIN = 0.2, FLOW_SPEED = 0.00075, TWINKLE_SPEED = 0.0014;
  let animationFrameId = null;
  let paused = false;
  function resize() {
    const pixelRatio = Math.min(window.devicePixelRatio || 1, 1.25);
    canvas.width = Math.floor(window.innerWidth * pixelRatio);
    canvas.height = Math.floor(window.innerHeight * pixelRatio);
    canvas.style.width = window.innerWidth + 'px';
    canvas.style.height = window.innerHeight + 'px';
    ctx.setTransform(pixelRatio, 0, 0, pixelRatio, 0, 0);
  }
  function seed() { nodes.length = 0; for (let i = 0; i < NODE_COUNT; i += 1) nodes.push({ x: Math.random() * window.innerWidth, y: Math.random() * window.innerHeight, vx: (Math.random() - 0.5) * 0.2, vy: (Math.random() - 0.5) * 0.2, r: Math.random() * 1.4 + 1.0 }); }
  function drawNode(n, g) { const a = 0.36 + g * 0.58, blur = 7 + g * 15; ctx.save(); ctx.globalAlpha = a; ctx.fillStyle = '#d7c7ff'; ctx.shadowColor = '#a689ff'; ctx.shadowBlur = blur; ctx.beginPath(); ctx.arc(n.x, n.y, n.r + g * 0.78, 0, Math.PI * 2); ctx.fill(); ctx.lineWidth = 0.9; ctx.strokeStyle = 'rgba(222, 214, 255,' + (0.26 + g * 0.42) + ')'; ctx.beginPath(); ctx.moveTo(n.x - STAR_CROSS_SIZE, n.y); ctx.lineTo(n.x + STAR_CROSS_SIZE, n.y); ctx.moveTo(n.x, n.y - STAR_CROSS_SIZE); ctx.lineTo(n.x, n.y + STAR_CROSS_SIZE); ctx.stroke(); ctx.restore(); }
  function drawAura(t) { const p = (Math.sin(t * 0.0011) + 1) * 0.5, core = 110 + p * 30, halo = 250 + p * 58; const grad = ctx.createRadialGradient(pointer.x, pointer.y, 0, pointer.x, pointer.y, halo); grad.addColorStop(0, 'rgba(165, 135, 255,' + (0.08 + p * 0.06) + ')'); grad.addColorStop(0.38, 'rgba(108, 150, 255, 0.06)'); grad.addColorStop(0.78, 'rgba(86, 54, 142, 0.03)'); grad.addColorStop(1, 'rgba(28, 18, 48, 0)'); ctx.save(); ctx.globalCompositeOperation = 'lighter'; ctx.fillStyle = grad; ctx.beginPath(); ctx.arc(pointer.x, pointer.y, halo, 0, Math.PI * 2); ctx.fill(); ctx.strokeStyle = 'rgba(196, 182, 255,' + (0.05 + p * 0.06) + ')'; ctx.lineWidth = 1.0; ctx.beginPath(); ctx.arc(pointer.x, pointer.y, core, 0, Math.PI * 2); ctx.stroke(); ctx.restore(); }
  function drawTraveler(a, b, progress, intensity) { const x = a.x + (b.x - a.x) * progress, y = a.y + (b.y - a.y) * progress; ctx.save(); ctx.globalAlpha = 0.14 + intensity * 0.32; ctx.fillStyle = '#efeaff'; ctx.shadowColor = 'rgba(184, 162, 255, 0.7)'; ctx.shadowBlur = 6 + intensity * 10; ctx.beginPath(); ctx.arc(x, y, 0.9 + intensity * 1.1, 0, Math.PI * 2); ctx.fill(); ctx.restore(); }
  function frame(time = 0) {
    if (paused) return;
    ctx.clearRect(0, 0, window.innerWidth, window.innerHeight); document.body.classList.add('network-theme-active'); drawAura(time);
    for (const n of nodes) { n.x += n.vx; n.y += n.vy; if (n.x < 0 || n.x > window.innerWidth) n.vx *= -1; if (n.y < 0 || n.y > window.innerHeight) n.vy *= -1; }
    for (let i = 0; i < nodes.length; i += 1) { const a = nodes[i]; const gA = Math.max(0, 1 - Math.hypot(a.x - pointer.x, a.y - pointer.y) / CURSOR_INFLUENCE);
      for (let j = i + 1; j < nodes.length; j += 1) { const b = nodes[j]; const d = Math.hypot(a.x - b.x, a.y - b.y); if (d > LINK_DISTANCE) continue; const gB = Math.max(0, 1 - Math.hypot(b.x - pointer.x, b.y - pointer.y) / CURSOR_INFLUENCE); const c = Math.max(gA, gB); const base = 0.05 + (1 - d / LINK_DISTANCE) * 0.18; const phase = (time * WAVE_SPEED) - (d * WAVE_LENGTH) + (i * 0.41 + j * 0.63); const w = (Math.sin(phase) + 1) * 0.5; const wg = WAVE_MIN + w * (1 - WAVE_MIN); const alpha = base + c * 0.30 + wg * 0.18; const width = 0.54 + c * 1.12 + wg * 0.45; const prog = (time * FLOW_SPEED + ((i * 0.09 + j * 0.17) % 1)) % 1; ctx.beginPath(); ctx.moveTo(a.x, a.y); ctx.lineTo(b.x, b.y); ctx.lineWidth = width; ctx.strokeStyle = 'rgba(173, 156, 255,' + alpha + ')'; ctx.shadowColor = 'rgba(159, 132, 255, 0.6)'; ctx.shadowBlur = 1.6 + c * 7.8 + wg * 5.6; ctx.stroke(); if (((i + j) % 7 === 0) && d < LINK_DISTANCE * 0.75) drawTraveler(a, b, prog, c * 0.55 + wg * 0.35); } }
    for (const n of nodes) { const g = Math.max(0, 1 - Math.hypot(n.x - pointer.x, n.y - pointer.y) / CURSOR_INFLUENCE); const tw = (Math.sin((time * TWINKLE_SPEED) + n.x * 0.02 + n.y * 0.017) + 1) * 0.5; drawNode(n, Math.min(1, g + tw * 0.22)); }
    animationFrameId = requestAnimationFrame(frame);
  }
  window.addEventListener('mousemove', (e) => { pointer.x = e.clientX; pointer.y = e.clientY; });
  window.addEventListener('resize', () => { resize(); seed(); });
  document.addEventListener('visibilitychange', () => {
    paused = document.hidden;
    if (!paused && !animationFrameId) {
      animationFrameId = requestAnimationFrame(frame);
    } else if (paused && animationFrameId) {
      cancelAnimationFrame(animationFrameId);
      animationFrameId = null;
    }
  });
  resize(); seed(); frame();
})();
