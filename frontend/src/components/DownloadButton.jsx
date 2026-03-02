import React from 'react';

export default function DownloadButton({ pdfBlob, onReset }) {
    const handleDownload = () => {
        if (!pdfBlob) return;
        const url = URL.createObjectURL(pdfBlob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'technical-paper.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    };

    return (
        <div className="flex flex-col sm:flex-row items-center gap-3 mt-4">
            <button
                id="download-btn"
                onClick={handleDownload}
                className="btn-primary rounded-xl px-8 py-3.5 text-sm font-bold text-white
                   flex items-center gap-2.5 w-full sm:w-auto justify-center"
            >
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                        d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                </svg>
                Download PDF
            </button>

            <button
                id="reset-btn"
                onClick={onReset}
                className="rounded-xl px-6 py-3.5 text-sm font-semibold text-slate-400
                   border border-slate-700 hover:border-slate-500 hover:text-slate-200
                   transition-all duration-200 flex items-center gap-2 w-full sm:w-auto justify-center"
            >
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                        d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
                Generate Another
            </button>
        </div>
    );
}
