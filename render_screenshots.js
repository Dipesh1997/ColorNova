const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';
const outputDir = path.resolve('play_store_assets');

// Common SVGs
const icons = {
  palette: `<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="13.5" cy="6.5" r=".5" fill="currentColor"/><circle cx="17.5" cy="10.5" r=".5" fill="currentColor"/><circle cx="8.5" cy="7.5" r=".5" fill="currentColor"/><circle cx="6.5" cy="12.5" r=".5" fill="currentColor"/><path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10c.926 0 1.648-.746 1.648-1.688 0-.437-.18-.835-.437-1.125-.29-.289-.438-.652-.438-1.125a1.64 1.64 0 0 1 1.668-1.668h1.996c3.051 0 5.563-2.512 5.563-5.563C22 6.5 17.5 2 12 2z"/></svg>`,
  search: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>`,
  clear: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6 6 18M6 6l12 12"/></svg>`,
  sun: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/></svg>`,
  moon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/></svg>`,
  sparkle: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m12 3-1.9 5.8a2 2 0 0 1-1.3 1.3L3 12l5.8 1.9a2 2 0 0 1 1.3 1.3L12 21l1.9-5.8a2 2 0 0 1 1.3-1.3L21 12l-5.8-1.9a2 2 0 0 1-1.3-1.3L12 3z"/></svg>`,
  copy: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect width="14" height="14" x="8" y="8" rx="2" ry="2"/><path d="M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2"/></svg>`,
  heart: `<svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"/></svg>`,
  heartOutline: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"/></svg>`,
  filter: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/></svg>`,
  check: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>`,
  wifi: `<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 18c1.1 0 2 .9 2 2s-.9 2-2 2-2-.9-2-2 .9-2 2-2zm-4.9-3.7 1.4 1.4C9.6 14.6 10.7 14 12 14s2.4.6 3.5 1.7l1.4-1.4C15.4 12.8 13.8 12 12 12s-3.4.8-4.9 2.3zm-3.6-3.6 1.4 1.4C6.7 10.3 9.2 9 12 9s5.3 1.3 7.1 3.1l1.4-1.4C18.2 8.4 15.3 7 12 7s-6.2 1.4-8.5 3.7z"/></svg>`,
  signal: `<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><rect x="2" y="16" width="3" height="5" rx="1"/><rect x="7" y="12" width="3" height="9" rx="1"/><rect x="12" y="8" width="3" height="13" rx="1"/><rect x="17" y="4" width="3" height="17" rx="1"/></svg>`,
  battery: `<svg width="22" height="14" viewBox="0 0 24 14" fill="currentColor"><rect x="1" y="1" width="18" height="12" rx="3" fill="none" stroke="currentColor" stroke-width="2"/><rect x="3" y="3" width="12" height="8" rx="1.5"/><path d="M21 5v4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>`
};

function renderPhoneMockup({
  activeTab = 'LIGHT', // LIGHT, MEDIUM, DARK, COMBINATION
  searchQuery = '',
  activeFilter = 'All',
  contentHtml = '',
  overlayHtml = ''
}) {
  const tabs = [
    { id: 'LIGHT', name: 'Light', color: '#FFD54F' },
    { id: 'MEDIUM', name: 'Medium', color: '#FF6B6B' },
    { id: 'DARK', name: 'Dark', color: '#A18CD1' },
    { id: 'COMBINATION', name: 'Combos', color: '#4ECDC4' }
  ];

  return `
    <div class="phone-frame">
      <div class="phone-inner">
        <!-- Status Bar -->
        <div class="status-bar">
          <div class="time">9:41</div>
          <div class="camera-notch">
            <div class="camera-lens"></div>
          </div>
          <div class="status-icons">
            <span class="icon">${icons.signal}</span>
            <span class="icon">${icons.wifi}</span>
            <span class="icon">${icons.battery}</span>
          </div>
        </div>

        <!-- Search Bar (Header removed) -->
        <div class="search-container" style="padding-top: 10px;">
          <div class="search-box ${searchQuery ? 'has-text' : ''}">
            <div class="search-icon">${icons.search}</div>
            <div class="search-input ${searchQuery ? 'value' : 'placeholder'}">
              ${searchQuery || 'Search by name, code, #hex, or page (e.g. 60)...'}
            </div>
            ${searchQuery ? `<div class="clear-icon">${icons.clear}</div>` : ''}
          </div>
        </div>

        <!-- Tab Bar: Text Only -->
        <div class="tabs-bar">
          ${tabs.map(tab => {
            const isActive = tab.id === activeTab;
            return `
              <div class="tab-item ${isActive ? 'active' : ''}">
                <div class="tab-label ${isActive ? 'active' : ''}" style="color: ${isActive ? tab.color : '#94A3B8'}; font-weight: ${isActive ? '700' : '500'}; font-size: 13.5px; padding: 10px 0;">${tab.name}</div>
                ${isActive ? `<div class="tab-indicator" style="background: ${tab.color};"></div>` : ''}
              </div>
            `;
          }).join('')}
        </div>

        <!-- Page Cards Slider -->
        <div class="page-slider">
          ${(() => {
            const pages = activeTab === 'LIGHT' ? [1, 2, 3, 4, 5, 6, 7] :
                          activeTab === 'DARK' ? [183, 184, 185, 186, 187, 188] :
                          activeTab === 'COMBINATION' ? [208, 209, 210, 211, 212, 213] :
                          [58, 59, 60, 61, 62, 63, 64];
            return pages.map(p => {
              const isActive = (activeTab === 'LIGHT' && p === 5) || (activeTab === 'MEDIUM' && p === 60) || (activeTab === 'DARK' && p === 183) || (activeTab === 'COMBINATION' && p === 208);
              return `
                <div class="page-card ${isActive ? 'active' : ''}">
                  <div class="page-title ${isActive ? 'active' : ''}">P.${p}</div>
                  <div class="page-swatch-strip">
                    <span style="background: ${activeTab === 'DARK' ? '#1E293B' : activeTab === 'COMBINATION' ? '#DFE6EE' : '#F6F0E2'};"></span>
                    <span style="background: ${activeTab === 'DARK' ? '#334155' : activeTab === 'COMBINATION' ? '#CAB1DA' : '#E8E5DC'};"></span>
                    <span style="background: ${activeTab === 'DARK' ? '#475569' : activeTab === 'COMBINATION' ? '#73618D' : '#89A763'};"></span>
                    <span style="background: ${activeTab === 'DARK' ? '#0F172A' : activeTab === 'COMBINATION' ? '#4A8348' : '#754D46'};"></span>
                  </div>
                </div>
              `;
            }).join('');
          })()}
        </div>

        <!-- Main Screen Body Content -->
        <div class="app-body">
          ${contentHtml}
        </div>

        ${overlayHtml ? `<div class="overlay-container">${overlayHtml}</div>` : ''}

        <!-- Home Bar Indicator -->
        <div class="home-indicator"></div>
      </div>
    </div>
  `;
}

function generateHtmlPage({
  headline,
  subtitle,
  tagText,
  tagColor,
  bgGlow1,
  bgGlow2,
  badgeText1,
  badgeText2,
  phoneProps
}) {
  return `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>ColorNova Screenshot</title>
<style>
  @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800;900&family=JetBrains+Mono:wght@500;700&display=swap');

  * { box-sizing: border-box; margin: 0; padding: 0; }

  body {
    width: 1080px;
    height: 1920px;
    background: #080B14;
    color: #F8FAFC;
    font-family: 'Plus Jakarta Sans', -apple-system, sans-serif;
    position: relative;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  /* Vibrant Background Ambient Lighting */
  .bg-glow-1 {
    position: absolute;
    top: -120px;
    left: -100px;
    width: 700px;
    height: 700px;
    background: radial-gradient(circle, ${bgGlow1} 0%, rgba(0,0,0,0) 70%);
    opacity: 0.35;
    pointer-events: none;
    filter: blur(80px);
  }

  .bg-glow-2 {
    position: absolute;
    top: 250px;
    right: -120px;
    width: 650px;
    height: 650px;
    background: radial-gradient(circle, ${bgGlow2} 0%, rgba(0,0,0,0) 70%);
    opacity: 0.35;
    pointer-events: none;
    filter: blur(85px);
  }

  .bg-grid {
    position: absolute;
    inset: 0;
    background-image: 
      linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
    background-size: 60px 60px;
    pointer-events: none;
    opacity: 0.6;
  }

  /* Poster Marketing Header */
  .poster-header {
    width: 960px;
    margin-top: 60px;
    margin-bottom: 24px;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    z-index: 10;
  }

  .category-pill {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 6px 18px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.12);
    font-size: 13px;
    font-weight: 700;
    letter-spacing: 1.5px;
    color: ${tagColor};
    text-transform: uppercase;
    margin-bottom: 16px;
    backdrop-filter: blur(10px);
  }

  .pill-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: ${tagColor};
    box-shadow: 0 0 10px ${tagColor};
  }

  .headline {
    font-size: 54px;
    font-weight: 900;
    line-height: 1.15;
    letter-spacing: -1px;
    background: linear-gradient(180deg, #FFFFFF 30%, #CBD5E1 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    max-width: 900px;
  }

  .subtitle {
    margin-top: 14px;
    font-size: 23px;
    font-weight: 500;
    color: #94A3B8;
    max-width: 820px;
    line-height: 1.35;
  }

  .feature-badges-row {
    margin-top: 20px;
    display: flex;
    gap: 12px;
  }

  .feature-badge {
    display: flex;
    align-items: center;
    gap: 8px;
    background: rgba(30, 41, 59, 0.7);
    border: 1px solid rgba(255, 255, 255, 0.1);
    padding: 8px 16px;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 600;
    color: #E2E8F0;
  }

  .badge-icon {
    color: #4ECDC4;
    display: flex;
    align-items: center;
  }

  /* Realistic Phone Frame */
  .phone-container {
    width: 820px;
    height: 1480px;
    margin-top: 10px;
    position: relative;
    z-index: 10;
  }

  .phone-frame {
    width: 100%;
    height: 100%;
    background: #0B0E17;
    border: 11px solid #1E273D;
    border-radius: 54px;
    box-shadow: 
      0 0 0 2px rgba(255, 255, 255, 0.08),
      0 35px 80px -15px rgba(0, 0, 0, 0.8),
      0 0 60px rgba(78, 205, 196, 0.12);
    overflow: hidden;
    position: relative;
    padding: 0;
  }

  .phone-inner {
    width: 100%;
    height: 100%;
    background: #0D111D;
    display: flex;
    flex-direction: column;
    position: relative;
    overflow: hidden;
  }

  /* Status Bar */
  .status-bar {
    height: 44px;
    padding: 0 24px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    font-weight: 700;
    color: #F8FAFC;
    z-index: 20;
  }

  .camera-notch {
    width: 90px;
    height: 22px;
    background: #05070B;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .camera-lens {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: #111827;
    border: 1.5px solid #1F2937;
  }

  .status-icons {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  /* App Header */
  .app-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 20px 8px 20px;
  }

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .brand-icon {
    width: 38px;
    height: 38px;
    border-radius: 12px;
    background: linear-gradient(135deg, #FF6B6B 0%, #4ECDC4 50%, #A18CD1 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    box-shadow: 0 4px 12px rgba(78, 205, 196, 0.35);
  }

  .brand-title {
    font-size: 19px;
    font-weight: 800;
    letter-spacing: 0.3px;
    color: #F8FAFC;
  }

  .brand-sub {
    font-size: 11px;
    color: #4ECDC4;
    font-weight: 600;
  }

  .header-badge {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 5px 10px;
    background: #1F273D;
    border: 1px solid #2B3652;
    border-radius: 10px;
    font-size: 11px;
    font-weight: 600;
    color: #F8FAFC;
  }

  .status-dot {
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: #4ECDC4;
  }

  /* Search Container */
  .search-container {
    padding: 6px 18px 10px 18px;
  }

  .search-box {
    background: #161C2C;
    border: 1.2px solid #2B3652;
    border-radius: 16px;
    height: 48px;
    display: flex;
    align-items: center;
    padding: 0 16px;
    gap: 12px;
  }

  .search-box.has-text {
    border-color: #4ECDC4;
    box-shadow: 0 0 0 2px rgba(78, 205, 196, 0.15);
  }

  .search-icon {
    color: #4ECDC4;
    display: flex;
    align-items: center;
  }

  .search-input {
    flex: 1;
    font-size: 13px;
    font-weight: 500;
  }

  .search-input.placeholder {
    color: #64748B;
  }

  .search-input.value {
    color: #F8FAFC;
    font-weight: 600;
  }

  .clear-icon {
    color: #94A3B8;
  }

  /* Tabs Bar */
  .tabs-bar {
    display: flex;
    border-bottom: 1px solid #2B3652;
    padding: 0 8px;
    position: relative;
  }

  .tab-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 8px 0 10px 0;
    position: relative;
    gap: 2px;
  }

  .tab-icon {
    display: flex;
    align-items: center;
  }

  .tab-label {
    font-size: 12px;
    font-weight: 600;
    color: #94A3B8;
  }

  .tab-label.active {
    font-weight: 700;
    color: #F8FAFC;
  }

  .tab-badge {
    font-size: 8.5px;
    padding: 1px 5px;
    border-radius: 6px;
    background: #161C2C;
    color: #64748B;
    font-weight: 600;
  }

  .tab-badge.active {
    background: #1F273D;
    color: #4ECDC4;
  }

  .tab-indicator {
    position: absolute;
    bottom: -1px;
    left: 10px;
    right: 10px;
    height: 3px;
    border-radius: 3px 3px 0 0;
  }

  /* Page Slider */
  .page-slider {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    overflow-x: hidden;
  }

  .page-card {
    width: 52px;
    height: 50px;
    background: #161C2C;
    border: 1px solid #2B3652;
    border-radius: 10px;
    padding: 4px 5px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;
  }

  .page-card.active {
    background: #1F273D;
    border-color: #4ECDC4;
    border-width: 1.5px;
  }

  .page-title {
    font-size: 11px;
    font-weight: 600;
    color: #F8FAFC;
  }

  .page-title.active {
    color: #4ECDC4;
    font-weight: 700;
  }

  .page-swatch-strip {
    display: flex;
    width: 100%;
    height: 10px;
    border-radius: 3px;
    overflow: hidden;
    border: 0.5px solid rgba(255, 255, 255, 0.1);
  }

  .page-swatch-strip span {
    flex: 1;
    height: 100%;
  }

  .swatch-page-badge, .swatch-tag, .swatch-code-badge {
    display: none;
  }

  /* App Body & Grid */
  .app-body {
    flex: 1;
    overflow-y: hidden;
    padding: 8px 16px 20px 16px;
  }

  .colors-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .color-card {
    background: #161C2C;
    border: 1px solid #242E46;
    border-radius: 18px;
    padding: 10px;
    display: flex;
    flex-direction: column;
  }

  .swatch-box {
    height: 116px;
    border-radius: 14px;
    position: relative;
    overflow: hidden;
    border: 1px solid rgba(255, 255, 255, 0.08);
  }

  .swatch-page-badge {
    position: absolute;
    top: 6px;
    left: 6px;
    padding: 2px 7px;
    background: rgba(0, 0, 0, 0.55);
    border-radius: 6px;
    font-size: 10px;
    font-weight: 700;
    color: white;
  }

  .swatch-tag {
    position: absolute;
    bottom: 6px;
    left: 6px;
    padding: 2px 6px;
    background: rgba(0, 0, 0, 0.55);
    border-radius: 6px;
    font-size: 9px;
    font-weight: 600;
    color: white;
  }

  .swatch-code-badge {
    position: absolute;
    bottom: 6px;
    right: 6px;
    padding: 2px 6px;
    background: rgba(0, 0, 0, 0.55);
    border-radius: 6px;
    font-size: 9px;
    font-family: 'JetBrains Mono', monospace;
    font-weight: 700;
    color: white;
  }

  .swatch-heart {
    position: absolute;
    top: 6px;
    right: 6px;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #FFF;
  }

  .swatch-heart.active {
    color: #FF6B6B;
  }

  .color-info {
    margin-top: 8px;
  }

  .color-name {
    font-size: 14px;
    font-weight: 800;
    color: #F8FAFC;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .color-sub {
    font-size: 10.5px;
    color: #94A3B8;
    margin-top: 2px;
  }

  .color-row {
    margin-top: 6px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .hex-badge {
    padding: 3px 8px;
    border-radius: 8px;
    background: #1F273D;
    border: 0.8px solid #2B3652;
    font-family: 'JetBrains Mono', monospace;
    font-size: 11.5px;
    font-weight: 700;
    color: #4ECDC4;
  }

  .copy-btn {
    width: 26px;
    height: 26px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94A3B8;
  }

  /* Combination Palettes List */
  .palettes-list {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .palette-card {
    background: #161C2C;
    border: 1px solid #242E46;
    border-radius: 20px;
    padding: 14px;
  }

  .palette-top {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .palette-name {
    font-size: 17px;
    font-weight: 800;
    color: #F8FAFC;
  }

  .palette-sub {
    font-size: 11.5px;
    font-weight: 600;
    color: #4ECDC4;
    margin-top: 2px;
  }

  .palette-copy-all {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 5px 10px;
    border-radius: 10px;
    background: #1F273D;
    color: #F8FAFC;
    font-size: 11px;
    font-weight: 700;
  }

  .palette-strip {
    height: 66px;
    border-radius: 12px;
    overflow: hidden;
    display: flex;
    margin-top: 12px;
    border: 1px solid rgba(255, 255, 255, 0.1);
  }

  .palette-segment {
    flex: 1;
    display: flex;
    align-items: flex-end;
    justify-content: center;
    position: relative;
  }

  .segment-hex {
    width: 100%;
    background: rgba(0, 0, 0, 0.5);
    text-align: center;
    padding: 3px 0;
    font-family: 'JetBrains Mono', monospace;
    font-size: 9px;
    font-weight: 700;
    color: white;
  }

  .palette-desc {
    margin-top: 9px;
    font-size: 12px;
    color: #94A3B8;
    line-height: 1.4;
  }

  .palette-pills {
    display: flex;
    gap: 6px;
    margin-top: 9px;
    overflow-x: hidden;
  }

  .color-pill-tag {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 3px 8px;
    border-radius: 8px;
    background: #1F273D;
    border: 1px solid #2B3652;
    font-size: 10px;
    color: #CBD5E1;
    white-space: nowrap;
  }

  .pill-color-dot {
    width: 7px;
    height: 7px;
    border-radius: 50%;
  }

  /* Overlay Modal */
  .overlay-container {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: #161C2C;
    border-radius: 32px 32px 0 0;
    box-shadow: 0 -15px 40px rgba(0, 0, 0, 0.75);
    border-top: 1px solid #2B3652;
    padding: 16px 20px 24px 20px;
    z-index: 50;
  }

  .modal-handle {
    width: 44px;
    height: 4.5px;
    border-radius: 3px;
    background: #2B3652;
    margin: 0 auto 14px auto;
  }

  .modal-swatch {
    height: 120px;
    border-radius: 18px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    border: 1px solid rgba(255, 255, 255, 0.15);
  }

  .modal-swatch-name {
    font-size: 21px;
    font-weight: 900;
  }

  .modal-swatch-sub {
    font-size: 13px;
    font-weight: 600;
    margin-top: 2px;
    opacity: 0.9;
  }

  .modal-swatch-hex {
    font-size: 16px;
    font-family: 'JetBrains Mono', monospace;
    font-weight: 700;
    margin-top: 4px;
  }

  .modal-codes-table {
    margin-top: 14px;
    display: flex;
    flex-direction: column;
    gap: 7px;
  }

  .modal-code-row {
    background: #1F273D;
    border: 1px solid #2B3652;
    border-radius: 12px;
    padding: 9px 12px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .modal-code-left {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .modal-code-lbl {
    font-size: 12px;
    font-weight: 800;
    color: #4ECDC4;
    width: 44px;
  }

  .modal-code-val {
    font-family: 'JetBrains Mono', monospace;
    font-size: 13px;
    font-weight: 600;
    color: #F8FAFC;
  }

  .wcag-row {
    display: flex;
    gap: 10px;
    margin-top: 12px;
  }

  .wcag-box {
    flex: 1;
    padding: 10px;
    border-radius: 10px;
    font-weight: 700;
    font-size: 12px;
    text-align: center;
  }

  /* Android Home Bar */
  .home-indicator {
    width: 140px;
    height: 4.5px;
    border-radius: 4px;
    background: #64748B;
    margin: 8px auto 10px auto;
  }
</style>
</head>
<body>
  <div class="bg-glow-1"></div>
  <div class="bg-glow-2"></div>
  <div class="bg-grid"></div>

  <!-- Poster Marketing Header -->
  <div class="poster-header">
    <div class="category-pill">
      <span class="pill-dot"></span>
      <span>${tagText}</span>
    </div>
    <h1 class="headline">${headline}</h1>
    <p class="subtitle">${subtitle}</p>
    <div class="feature-badges-row">
      <div class="feature-badge">
        <span class="badge-icon">${icons.check}</span>
        <span>${badgeText1}</span>
      </div>
      <div class="feature-badge">
        <span class="badge-icon">${icons.check}</span>
        <span>${badgeText2}</span>
      </div>
    </div>
  </div>

  <!-- Realistic Smartphone Mockup -->
  <div class="phone-container">
    ${renderPhoneMockup(phoneProps)}
  </div>
</body>
</html>`;
}

// ----------------------------------------------------
// SCREENSHOT 1: LIGHT TAB & SEARCH (Pages 1-17)
// ----------------------------------------------------
const lightColorsHtml = `
  <div class="colors-grid">
    <div class="color-card">
      <div class="swatch-box" style="background: #F7F1E7;">
        <div class="swatch-page-badge">Page 1</div>
        <div class="swatch-tag">Pastel • Warm</div>
        <div class="swatch-code-badge">L146</div>
        <div class="swatch-heart active">${icons.heart}</div>
      </div>
      <div class="color-info">
        <div class="color-name">SONNET</div>
        <div class="color-sub">Code: L146 • Page 1</div>
        <div class="color-row">
          <div class="hex-badge">#F7F1E7</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #F7F0E6;">
        <div class="swatch-page-badge">Page 1</div>
        <div class="swatch-tag">Pastel • Warm</div>
        <div class="swatch-code-badge">L145</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">WHITE CAMEO</div>
        <div class="color-sub">Code: L145 • Page 1</div>
        <div class="color-row">
          <div class="hex-badge">#F7F0E6</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #F0EEE8;">
        <div class="swatch-page-badge">Page 1</div>
        <div class="swatch-tag">Pastel • Neutral</div>
        <div class="swatch-code-badge">L162</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">BIENNALE WHITE</div>
        <div class="color-sub">Code: L162 • Page 1</div>
        <div class="color-row">
          <div class="hex-badge">#F0EEE8</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #EDEBE3;">
        <div class="swatch-page-badge">Page 2</div>
        <div class="swatch-tag">Pastel • Minimal</div>
        <div class="swatch-code-badge">8260</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">CAMPHOR</div>
        <div class="color-sub">Code: 8260 • Page 2</div>
        <div class="color-row">
          <div class="hex-badge">#EDEBE3</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #F1ECE2;">
        <div class="swatch-page-badge">Page 3</div>
        <div class="swatch-tag">Pastel • Warm</div>
        <div class="swatch-code-badge">765</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">MORNING GLORY</div>
        <div class="color-sub">Code: 765 • Page 3</div>
        <div class="color-row">
          <div class="hex-badge">#F1ECE2</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #E8E5DC;">
        <div class="swatch-page-badge">Page 5</div>
        <div class="swatch-tag">Pastel • Minimal</div>
        <div class="swatch-code-badge">8267</div>
        <div class="swatch-heart active">${icons.heart}</div>
      </div>
      <div class="color-info">
        <div class="color-name">WHITE CHEESE</div>
        <div class="color-sub">Code: 8267 • Page 5</div>
        <div class="color-row">
          <div class="hex-badge">#E8E5DC</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>
  </div>
`;

const page1Html = generateHtmlPage({
  headline: 'Explore 102 Light Shades',
  subtitle: 'Spectra Fan Deck Pages 1 to 17 • Curate soft off-whites & pastels with page numbers',
  tagText: 'LIGHT TAB • PAGES 1 TO 17',
  tagColor: '#FFD54F',
  bgGlow1: 'rgba(255, 213, 79, 0.4)',
  bgGlow2: 'rgba(78, 205, 196, 0.25)',
  badgeText1: '102 Asian Paints Shades',
  badgeText2: 'Page Number on Every Shade',
  phoneProps: {
    activeTab: 'LIGHT',
    searchQuery: '',
    activeFilter: 'Pastel',
    contentHtml: lightColorsHtml
  }
});

// ----------------------------------------------------
// SCREENSHOT 2: MEDIUM TAB WITH COLOR INSPECTION (Pages 18-182)
// ----------------------------------------------------
const mediumColorsHtml = `
  <div class="colors-grid">
    <div class="color-card">
      <div class="swatch-box" style="background: #89A763;">
        <div class="swatch-page-badge">Page 60</div>
        <div class="swatch-tag">Earth • Vibrant</div>
        <div class="swatch-code-badge">7670</div>
        <div class="swatch-heart active">${icons.heart}</div>
      </div>
      <div class="color-info">
        <div class="color-name">GARDEN CRESS</div>
        <div class="color-sub">Code: 7670 • Page 60</div>
        <div class="color-row">
          <div class="hex-badge">#89A763</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #E9EEDB;">
        <div class="swatch-page-badge">Page 60</div>
        <div class="swatch-tag">Pastel • Cool</div>
        <div class="swatch-code-badge">7676</div>
        <div class="swatch-heart active">${icons.heart}</div>
      </div>
      <div class="color-info">
        <div class="color-name">GREEN REPUBLIC</div>
        <div class="color-sub">Code: 7676 • Page 60</div>
        <div class="color-row">
          <div class="hex-badge">#E9EEDB</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #D6E5C2;">
        <div class="swatch-page-badge">Page 60</div>
        <div class="swatch-tag">Pastel • Earth</div>
        <div class="swatch-code-badge">7674</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">ECHO OF SPRING</div>
        <div class="color-sub">Code: 7674 • Page 60</div>
        <div class="color-row">
          <div class="hex-badge">#D6E5C2</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #9EBC83;">
        <div class="swatch-page-badge">Page 60</div>
        <div class="swatch-tag">Earth • Cool</div>
        <div class="swatch-code-badge">2370</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">CASCADE GREEN</div>
        <div class="color-sub">Code: 2370 • Page 60</div>
        <div class="color-row">
          <div class="hex-badge">#9EBC83</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>
  </div>
`;

const inspectionModalHtml = `
  <div class="modal-handle"></div>
  <div class="modal-swatch" style="background: #89A763; color: #FFF;">
    <div class="modal-swatch-name">GARDEN CRESS</div>
    <div class="modal-swatch-sub">Shade 7670 • Page 60</div>
    <div class="modal-swatch-hex">#89A763</div>
  </div>
  <div class="modal-codes-table">
    <div class="modal-code-row">
      <div class="modal-code-left">
        <span class="modal-code-lbl">SHADE</span>
        <span class="modal-code-val">7670</span>
      </div>
      <span style="color:#4ECDC4">${icons.copy}</span>
    </div>
    <div class="modal-code-row">
      <div class="modal-code-left">
        <span class="modal-code-lbl">PAGE</span>
        <span class="modal-code-val">Page 60 (Asian Paints Spectra)</span>
      </div>
      <span style="color:#4ECDC4">${icons.copy}</span>
    </div>
    <div class="modal-code-row">
      <div class="modal-code-left">
        <span class="modal-code-lbl">HEX</span>
        <span class="modal-code-val">#89A763</span>
      </div>
      <span style="color:#4ECDC4">${icons.copy}</span>
    </div>
    <div class="modal-code-row">
      <div class="modal-code-left">
        <span class="modal-code-lbl">RGB</span>
        <span class="modal-code-val">rgb(137, 167, 99)</span>
      </div>
      <span style="color:#4ECDC4">${icons.copy}</span>
    </div>
  </div>
  <div class="wcag-row">
    <div class="wcag-box" style="background: #89A763; color: #FFF; border: 1px solid rgba(255,255,255,0.2);">
      White Text Preview
    </div>
    <div class="wcag-box" style="background: #89A763; color: #000; border: 1px solid rgba(0,0,0,0.2);">
      Black Text Preview
    </div>
  </div>
`;

const page2Html = generateHtmlPage({
  headline: '1,319 Medium Shades',
  subtitle: 'Spectra Fan Deck Pages 18 to 182 • Comprehensive inspection and codes',
  tagText: 'MEDIUM TAB • PAGES 18 TO 182',
  tagColor: '#FF6B6B',
  bgGlow1: 'rgba(255, 107, 107, 0.45)',
  bgGlow2: 'rgba(78, 205, 196, 0.3)',
  badgeText1: '1,319 Core Wall Shades',
  badgeText2: 'Hex, RGB, HSL & WCAG',
  phoneProps: {
    activeTab: 'MEDIUM',
    searchQuery: 'Garden Cress',
    activeFilter: 'Earth',
    contentHtml: mediumColorsHtml,
    overlayHtml: inspectionModalHtml
  }
});

// ----------------------------------------------------
// SCREENSHOT 3: DARK TAB (Pages 183-207)
// ----------------------------------------------------
const darkColorsHtml = `
  <div class="colors-grid">
    <div class="color-card">
      <div class="swatch-box" style="background: #AD98C9; border-color: #334155;">
        <div class="swatch-page-badge">Page 183</div>
        <div class="swatch-tag">Cool • Lavender</div>
        <div class="swatch-code-badge">9616</div>
        <div class="swatch-heart active">${icons.heart}</div>
      </div>
      <div class="color-info">
        <div class="color-name">Wisteria Lane</div>
        <div class="color-sub">Code: 9616 • Page 183</div>
        <div class="color-row">
          <div class="hex-badge">#AD98C9</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #A584B9; border-color: #3730A3;">
        <div class="swatch-page-badge">Page 183</div>
        <div class="swatch-tag">Cool • Purple</div>
        <div class="swatch-code-badge">9617</div>
        <div class="swatch-heart active">${icons.heart}</div>
      </div>
      <div class="color-info">
        <div class="color-name">Dianthus</div>
        <div class="color-sub">Code: 9617 • Page 183</div>
        <div class="color-row">
          <div class="hex-badge">#A584B9</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #2B2D3A; border-color: #581C87;">
        <div class="swatch-page-badge">Page 183</div>
        <div class="swatch-tag">Dark • Slate</div>
        <div class="swatch-code-badge">8432</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">NIGHT SKY</div>
        <div class="color-sub">Code: 8432 • Page 183</div>
        <div class="color-row">
          <div class="hex-badge">#2B2D3A</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #1F232B; border-color: #065F46;">
        <div class="swatch-page-badge">Page 183</div>
        <div class="swatch-tag">Dark • Noir</div>
        <div class="swatch-code-badge">8424</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">INK BLACK</div>
        <div class="color-sub">Code: 8424 • Page 183</div>
        <div class="color-row">
          <div class="hex-badge">#1F232B</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #1B1E24; border-color: #9F1239;">
        <div class="swatch-page-badge">Page 184</div>
        <div class="swatch-tag">Dark • Charcoal</div>
        <div class="swatch-code-badge">8240</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">OBSIDIAN SLATE</div>
        <div class="color-sub">Code: 8240 • Page 184</div>
        <div class="color-row">
          <div class="hex-badge">#1B1E24</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>

    <div class="color-card">
      <div class="swatch-box" style="background: #5F7549; border-color: #555;">
        <div class="swatch-page-badge">Page 185</div>
        <div class="swatch-tag">Dark • Earth</div>
        <div class="swatch-code-badge">7677</div>
        <div class="swatch-heart">${icons.heartOutline}</div>
      </div>
      <div class="color-info">
        <div class="color-name">DARK DRAMA</div>
        <div class="color-sub">Code: 7677 • Page 185</div>
        <div class="color-row">
          <div class="hex-badge">#5F7549</div>
          <div class="copy-btn">${icons.copy}</div>
        </div>
      </div>
    </div>
  </div>
`;

const page3Html = generateHtmlPage({
  headline: '200 Deep & Dark Tones',
  subtitle: 'Spectra Fan Deck Pages 183 to 207 • Deep moody accents & architectural slates',
  tagText: 'DARK TAB • PAGES 183 TO 207',
  tagColor: '#A18CD1',
  bgGlow1: 'rgba(161, 140, 209, 0.45)',
  bgGlow2: 'rgba(30, 27, 75, 0.4)',
  badgeText1: '200 Dramatic Accent Colors',
  badgeText2: 'Night & Midnight Palettes',
  phoneProps: {
    activeTab: 'DARK',
    searchQuery: '',
    activeFilter: 'All',
    contentHtml: darkColorsHtml
  }
});

// ----------------------------------------------------
// SCREENSHOT 4: COMBINATIONS TAB (Pages 208-225)
// ----------------------------------------------------
const combinationsHtml = `
  <div class="palettes-list">
    <div class="palette-card">
      <div class="palette-top">
        <div>
          <div class="palette-name">Page 208 Harmony</div>
          <div class="palette-sub">Spectra Combo • Page 208 • 7 Curated Shades</div>
        </div>
        <div class="palette-copy-all">
          <span style="color:#4ECDC4">${icons.copy}</span>
          <span>Copy 7</span>
        </div>
      </div>
      <div class="palette-strip">
        <div class="palette-segment" style="background: #DFE6EE;">
          <div class="segment-hex" style="color:#111;">#DFE6EE</div>
        </div>
        <div class="palette-segment" style="background: #E2BD89;">
          <div class="segment-hex" style="color:#111;">#E2BD89</div>
        </div>
        <div class="palette-segment" style="background: #CAB1DA;">
          <div class="segment-hex" style="color:#111;">#CAB1DA</div>
        </div>
        <div class="palette-segment" style="background: #73618D;">
          <div class="segment-hex">#73618D</div>
        </div>
        <div class="palette-segment" style="background: #D3EDB9;">
          <div class="segment-hex" style="color:#111;">#D3EDB9</div>
        </div>
        <div class="palette-segment" style="background: #4A8348;">
          <div class="segment-hex">#4A8348</div>
        </div>
        <div class="palette-segment" style="background: #754D46;">
          <div class="segment-hex">#754D46</div>
        </div>
      </div>
      <div class="palette-desc">
        Curated 7-color harmony strip from Asian Paints Spectra fan deck page 208: Stratos & Sands of Thar.
      </div>
      <div class="palette-pills">
        <div class="color-pill-tag">
          <div class="pill-color-dot" style="background:#DFE6EE"></div>
          <span>9645 Stratos (p. 208)</span>
        </div>
        <div class="color-pill-tag">
          <div class="pill-color-dot" style="background:#E2BD89"></div>
          <span>9879 Sands of Thar (p. 208)</span>
        </div>
        <div class="color-pill-tag">
          <div class="pill-color-dot" style="background:#4A8348"></div>
          <span>9764 Chalkboard Green</span>
        </div>
      </div>
    </div>

    <div class="palette-card">
      <div class="palette-top">
        <div>
          <div class="palette-name">Page 209 Harmony</div>
          <div class="palette-sub">Spectra Combo • Page 209 • 7 Curated Shades</div>
        </div>
        <div class="palette-copy-all">
          <span style="color:#4ECDC4">${icons.copy}</span>
          <span>Copy 7</span>
        </div>
      </div>
      <div class="palette-strip">
        <div class="palette-segment" style="background: #F6DDCF;">
          <div class="segment-hex" style="color:#111;">#F6DDCF</div>
        </div>
        <div class="palette-segment" style="background: #2B5971;">
          <div class="segment-hex">#2B5971</div>
        </div>
        <div class="palette-segment" style="background: #D2C5A9;">
          <div class="segment-hex" style="color:#111;">#D2C5A9</div>
        </div>
        <div class="palette-segment" style="background: #B99AAB;">
          <div class="segment-hex">#B99AAB</div>
        </div>
        <div class="palette-segment" style="background: #EDDAC7;">
          <div class="segment-hex" style="color:#111;">#EDDAC7</div>
        </div>
        <div class="palette-segment" style="background: #EADB75;">
          <div class="segment-hex" style="color:#111;">#EADB75</div>
        </div>
        <div class="palette-segment" style="background: #D1AC2E;">
          <div class="segment-hex">#D1AC2E</div>
        </div>
      </div>
      <div class="palette-desc">
        Curated 7-color harmony strip from Asian Paints Spectra fan deck page 209: Camellia Bud & Moonlight River.
      </div>
      <div class="palette-pills">
        <div class="color-pill-tag">
          <div class="pill-color-dot" style="background:#F6DDCF"></div>
          <span>K247 Camellia Bud</span>
        </div>
        <div class="color-pill-tag">
          <div class="pill-color-dot" style="background:#2B5971"></div>
          <span>9684 Moonlight River</span>
        </div>
        <div class="color-pill-tag">
          <div class="pill-color-dot" style="background:#D1AC2E"></div>
          <span>9864 Yellow Moss</span>
        </div>
      </div>
    </div>
  </div>
`;

const page4Html = generateHtmlPage({
  headline: '18 Curated Combos',
  subtitle: 'Spectra Fan Deck Pages 208 to 225 • 7-shade harmonized room combos',
  tagText: 'COMBO TAB • PAGES 208 TO 225',
  tagColor: '#4ECDC4',
  bgGlow1: 'rgba(78, 205, 196, 0.45)',
  bgGlow2: 'rgba(255, 107, 107, 0.3)',
  badgeText1: '18 Spectra Harmonies',
  badgeText2: '126 Curated Combination Shades',
  phoneProps: {
    activeTab: 'COMBINATION',
    searchQuery: '',
    activeFilter: 'All',
    contentHtml: combinationsHtml
  }
});

// Render function
function renderScreenshot(htmlContent, outputFileName) {
  const tempHtmlPath = path.resolve(`temp_${outputFileName}.html`);
  fs.writeFileSync(tempHtmlPath, htmlContent);
  const fileUrl = 'file:///' + tempHtmlPath.replace(/\\/g, '/');
  const targetPngPath = path.join(outputDir, outputFileName);

  console.log(`Rendering ${outputFileName}...`);
  execSync(`"${edgePath}" --headless --disable-gpu --screenshot="${targetPngPath}" --window-size=1080,1920 "${fileUrl}"`);
  fs.unlinkSync(tempHtmlPath);

  const stats = fs.statSync(targetPngPath);
  console.log(`✓ Generated ${outputFileName}: ${(stats.size / 1024).toFixed(1)} KB`);
}

// Ensure directory
if (!fs.existsSync(outputDir)) {
  fs.mkdirSync(outputDir, { recursive: true });
}

// Generate the 4 screenshots
renderScreenshot(page1Html, 'screenshot_1_light.png');
renderScreenshot(page2Html, 'screenshot_2_medium.png');
renderScreenshot(page3Html, 'screenshot_3_dark.png');
renderScreenshot(page4Html, 'screenshot_4_combination.png');

console.log('All 4 Play Store screenshots generated successfully with updated Asian Paints dataset!');
