import React, { useState, useCallback } from 'react';
import Hero from './components/Hero';
import UrlInput from './components/UrlInput';
import LoadingState from './components/LoadingState';
import PaperPreview from './components/PaperPreview';
import { generatePaper } from './services/api';

const APP_STATES = {
  IDLE: 'idle',
  LOADING: 'loading',
  DONE: 'done',
  ERROR: 'error',
};

export default function App() {
  const [appState, setAppState] = useState(APP_STATES.IDLE);
  const [pdfBlob, setPdfBlob] = useState(null);
  const [errorMsg, setErrorMsg] = useState('');
  const [sourceUrl, setSourceUrl] = useState('');

  const handleGenerate = useCallback(async (url) => {
    setAppState(APP_STATES.LOADING);
    setErrorMsg('');
    setSourceUrl(url);
    try {
      const blob = await generatePaper(url);
      setPdfBlob(blob);
      setAppState(APP_STATES.DONE);
    } catch (err) {
      setErrorMsg(err.message || 'Something went wrong. Please try again.');
      setAppState(APP_STATES.ERROR);
    }
  }, []);

  const handleReset = useCallback(() => {
    setAppState(APP_STATES.IDLE);
    setPdfBlob(null);
    setErrorMsg('');
    setSourceUrl('');
  }, []);

  return (
    <div className="min-h-screen relative overflow-hidden" style={{ background: 'var(--bg-primary)' }}>

      {/* Decorative background orbs */}
      <div className="orb w-96 h-96 bg-indigo-600 top-[-100px] left-[-100px]" />
      <div className="orb w-80 h-80 bg-purple-600 top-[30%] right-[-80px]" />
      <div className="orb w-64 h-64 bg-sky-600 bottom-[-60px] left-[30%]" />

      {/* Grid overlay */}
      <div className="absolute inset-0 opacity-[0.03]"
        style={{ backgroundImage: 'linear-gradient(#fff 1px, transparent 1px), linear-gradient(90deg, #fff 1px, transparent 1px)', backgroundSize: '40px 40px' }} />

      {/* Main content */}
      <main className="relative z-10 flex flex-col items-center min-h-screen px-4 py-16 md:py-24 gap-12">

        {/* Brand nav */}
        <nav className="flex items-center gap-2 self-start ml-4">
          <div className="w-7 h-7 rounded-lg bg-indigo-600 flex items-center justify-center text-xs font-black text-white">L</div>
          <span className="font-bold text-slate-300 text-sm tracking-wide">Link2Paper</span>
        </nav>

        {/* Hero */}
        <Hero />

        {/* Dynamic middle section */}
        <div className="w-full flex flex-col items-center gap-6">
          {appState === APP_STATES.IDLE && (
            <UrlInput onGenerate={handleGenerate} isLoading={false} />
          )}

          {appState === APP_STATES.LOADING && (
            <LoadingState />
          )}

          {appState === APP_STATES.DONE && (
            <PaperPreview
              pdfBlob={pdfBlob}
              sourceUrl={sourceUrl}
              onReset={handleReset}
            />
          )}

          {appState === APP_STATES.ERROR && (
            <div className="w-full max-w-lg animate-fade-in">
              <div className="glass-card rounded-2xl p-6 border-red-500/30">
                <div className="flex items-start gap-3">
                  <div className="w-9 h-9 rounded-full bg-red-500/15 flex items-center justify-center shrink-0">
                    <svg className="w-5 h-5 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                        d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.96-.833-2.732 0L3.062 16.5c-.77.833.192 2.5 1.732 2.5z" />
                    </svg>
                  </div>
                  <div>
                    <p className="text-sm font-semibold text-red-300">Generation Failed</p>
                    <p className="text-xs text-slate-400 mt-1">{errorMsg}</p>
                  </div>
                </div>
                <button
                  onClick={handleReset}
                  className="mt-4 w-full btn-primary rounded-xl py-2.5 text-sm font-bold text-white"
                >
                  Try Again
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <footer className="mt-auto text-xs text-slate-600 text-center">
          Link2Paper · AI Technical Paper Generator · Built with Spring Boot + React
        </footer>
      </main>
    </div>
  );
}
